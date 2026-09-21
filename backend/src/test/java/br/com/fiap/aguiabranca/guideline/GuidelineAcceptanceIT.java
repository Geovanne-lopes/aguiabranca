package br.com.fiap.aguiabranca.guideline;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.fiap.aguiabranca.AbstractMongoIT;
import br.com.fiap.aguiabranca.guideline.infra.GuidelineRepository;

@AutoConfigureMockMvc
class GuidelineAcceptanceIT extends AbstractMongoIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private GuidelineRepository guidelineRepository;

    @Test
    @DisplayName("A-GL-01 GET /guidelines autenticado 200 e não lista deletadas")
    void aGl01_listDoesNotIncludeDeleted() throws Exception {
        String operatorToken = login("operador@innovatecorp.com", "oper123");
        String leaderToken = login("lideranca@innovatecorp.com", "lider123");
        String title = "Diretriz temporária " + UUID.randomUUID();

        mockMvc.perform(get("/api/v1/guidelines")
                        .header("Authorization", "Bearer " + operatorToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.page").value(0));

        String id = createGuideline(leaderToken, title, "Conteúdo mínimo válido para a diretriz.");

        mockMvc.perform(delete("/api/v1/guidelines/" + id)
                        .header("Authorization", "Bearer " + leaderToken))
                .andExpect(status().isNoContent());

        MvcResult list = mockMvc.perform(get("/api/v1/guidelines")
                        .header("Authorization", "Bearer " + operatorToken)
                        .param("q", title))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode content = objectMapper.readTree(list.getResponse().getContentAsString()).get("content");
        for (JsonNode item : content) {
            assertThat(item.get("title").asText()).isNotEqualTo(title);
        }
    }

    @Test
    @DisplayName("A-GL-02 LEADER cria/edita/exclui; MANAGER recebe 403 nas escritas")
    void aGl02_leaderWritesManagerForbidden() throws Exception {
        String leaderToken = login("lideranca@innovatecorp.com", "lider123");
        String managerToken = login("gestor@innovatecorp.com", "gest123");
        String title = "Eixo líder " + UUID.randomUUID();
        String body = guidelineBody(title, "Conteúdo mínimo válido para a diretriz.", "DIGITAL", "2026-H2");

        MvcResult created = mockMvc.perform(post("/api/v1/guidelines")
                        .header("Authorization", "Bearer " + leaderToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.version").value(1))
                .andExpect(jsonPath("$.authorName").value("Mariana Costa"))
                .andReturn();
        String id = objectMapper.readTree(created.getResponse().getContentAsString()).get("id").asText();

        mockMvc.perform(put("/api/v1/guidelines/" + id)
                        .header("Authorization", "Bearer " + leaderToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(guidelineBody(title + " v2", "Conteúdo atualizado da diretriz estratégica.", "DIGITAL", "2026-H2")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.version").value(2));

        mockMvc.perform(delete("/api/v1/guidelines/" + id)
                        .header("Authorization", "Bearer " + leaderToken))
                .andExpect(status().isNoContent());

        mockMvc.perform(post("/api/v1/guidelines")
                        .header("Authorization", "Bearer " + managerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("FORBIDDEN"));

        mockMvc.perform(put("/api/v1/guidelines/" + id)
                        .header("Authorization", "Bearer " + managerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("FORBIDDEN"));

        mockMvc.perform(delete("/api/v1/guidelines/" + id)
                        .header("Authorization", "Bearer " + managerToken))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("FORBIDDEN"));
    }

    @Test
    @DisplayName("A-GL-03 histórico CREATED+UPDATED append-only para LEADER; MANAGER 403")
    void aGl03_historyAppendOnlyLeaderOnly() throws Exception {
        String leaderToken = login("lideranca@innovatecorp.com", "lider123");
        String managerToken = login("gestor@innovatecorp.com", "gest123");
        String title = "Histórico " + UUID.randomUUID();

        String id = createGuideline(leaderToken, title, "Conteúdo mínimo válido para a diretriz.");

        mockMvc.perform(put("/api/v1/guidelines/" + id)
                        .header("Authorization", "Bearer " + leaderToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(guidelineBody(title, "Primeira edição da diretriz estratégica.", "ESG", "NetZero")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.version").value(2));

        mockMvc.perform(put("/api/v1/guidelines/" + id)
                        .header("Authorization", "Bearer " + leaderToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(guidelineBody(title, "Segunda edição da diretriz estratégica.", "ESG", "NetZero")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.version").value(3));

        mockMvc.perform(get("/api/v1/guidelines/" + id + "/history")
                        .header("Authorization", "Bearer " + leaderToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(3))
                .andExpect(jsonPath("$.content[0].version").value(3))
                .andExpect(jsonPath("$.content[0].action").value("UPDATED"))
                .andExpect(jsonPath("$.content[1].version").value(2))
                .andExpect(jsonPath("$.content[1].action").value("UPDATED"))
                .andExpect(jsonPath("$.content[2].version").value(1))
                .andExpect(jsonPath("$.content[2].action").value("CREATED"))
                .andExpect(jsonPath("$.content[0].actorName").value("Mariana Costa"));

        mockMvc.perform(get("/api/v1/guidelines/" + id + "/history")
                        .header("Authorization", "Bearer " + managerToken))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("FORBIDDEN"));
    }

    @Test
    @DisplayName("A-SEC-02 OPERATOR POST /guidelines retorna 403 e não cria registro")
    void aSec02_operatorCannotCreateGuideline() throws Exception {
        String operatorToken = login("operador@innovatecorp.com", "oper123");
        String title = "Operador não cria " + UUID.randomUUID();

        mockMvc.perform(post("/api/v1/guidelines")
                        .header("Authorization", "Bearer " + operatorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(guidelineBody(title, "Conteúdo mínimo válido para a diretriz.", null, null)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("FORBIDDEN"));

        assertThat(guidelineRepository.findByTitle(title)).isEmpty();
    }

    private String createGuideline(String leaderToken, String title, String content) throws Exception {
        MvcResult created = mockMvc.perform(post("/api/v1/guidelines")
                        .header("Authorization", "Bearer " + leaderToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(guidelineBody(title, content, null, null)))
                .andExpect(status().isCreated())
                .andReturn();
        return objectMapper.readTree(created.getResponse().getContentAsString()).get("id").asText();
    }

    private String guidelineBody(String title, String content, String category, String campaign) {
        String categoryJson = category == null ? "null" : "\"" + category + "\"";
        String campaignJson = campaign == null ? "null" : "\"" + campaign + "\"";
        return """
                {"title":"%s","content":"%s","category":%s,"campaign":%s}
                """.formatted(title, content, categoryJson, campaignJson);
    }

    private String login(String email, String password) throws Exception {
        String body = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"%s","password":"%s"}
                                """.formatted(email, password)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        return objectMapper.readTree(body).get("accessToken").asText();
    }
}
