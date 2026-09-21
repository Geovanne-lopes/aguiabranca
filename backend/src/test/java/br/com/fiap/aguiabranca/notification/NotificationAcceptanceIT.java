package br.com.fiap.aguiabranca.notification;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

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

@AutoConfigureMockMvc
class NotificationAcceptanceIT extends AbstractMongoIT {

    private static final Map<String, Session> SESSIONS = new ConcurrentHashMap<>();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("A-NOTIF-01 operador com ideia aprovada recebe item IDEA_APPROVED")
    void aNotif01_approvedIdeaAppearsForOperator() throws Exception {
        Session operator = login("operador@innovatecorp.com", "oper123");
        Session manager = login("gestor@innovatecorp.com", "gest123");
        String title = "App fila " + UUID.randomUUID();

        JsonNode created = createIdea(operator.token(), title);
        String ideaId = created.get("id").asText();

        mockMvc.perform(patch("/api/v1/ideas/" + ideaId + "/status")
                        .header("Authorization", "Bearer " + manager.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"APPROVED\",\"justification\":null}"))
                .andExpect(status().isOk());

        MvcResult ideaResult = mockMvc.perform(get("/api/v1/ideas/" + ideaId)
                        .header("Authorization", "Bearer " + operator.token()))
                .andExpect(status().isOk())
                .andReturn();
        Instant decisionAt = Instant.parse(objectMapper.readTree(ideaResult.getResponse().getContentAsString()).get("updatedAt").asText());

        MvcResult notifications = mockMvc.perform(get("/api/v1/notifications")
                        .header("Authorization", "Bearer " + operator.token()))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode body = objectMapper.readTree(notifications.getResponse().getContentAsString());
        assertThat(body.get("unreadHint").asBoolean()).isTrue();
        assertThat(body.get("items").size()).isLessThanOrEqualTo(50);

        String expectedId = "idea-approved-" + ideaId;
        JsonNode match = null;
        for (JsonNode item : body.get("items")) {
            if (expectedId.equals(item.get("id").asText())) {
                match = item;
                break;
            }
        }
        assertThat(match).isNotNull();
        assertThat(match.get("type").asText()).isEqualTo("IDEA_APPROVED");
        assertThat(match.get("title").asText()).isEqualTo("Ideia aprovada");
        assertThat(match.get("body").asText()).contains(title);
        assertThat(Instant.parse(match.get("createdAt").asText())).isEqualTo(decisionAt);
    }

    @Test
    @DisplayName("Notificações exigem autenticação e o OpenAPI publica o GET")
    void notificationsRequireAuthAndAreInOpenApi() throws Exception {
        mockMvc.perform(get("/api/v1/notifications"))
                .andExpect(status().isUnauthorized());

        MvcResult docs = mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode paths = objectMapper.readTree(docs.getResponse().getContentAsString()).get("paths");
        assertThat(paths.get("/api/v1/notifications").get("get")).isNotNull();
    }

    private JsonNode createIdea(String token, String title) throws Exception {
        MvcResult created = mockMvc.perform(post("/api/v1/ideas")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"%s","description":"Descrição com tamanho válido para a ideia.","category":"TECHNOLOGY","guidelineId":null}
                                """.formatted(title)))
                .andExpect(status().isCreated())
                .andReturn();
        return objectMapper.readTree(created.getResponse().getContentAsString());
    }

    private Session login(String email, String password) throws Exception {
        Session cached = SESSIONS.get(email);
        if (cached != null) {
            return cached;
        }
        synchronized (SESSIONS) {
            cached = SESSIONS.get(email);
            if (cached != null) {
                return cached;
            }
            MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {"email":"%s","password":"%s"}
                                    """.formatted(email, password)))
                    .andExpect(status().isOk())
                    .andReturn();
            JsonNode body = objectMapper.readTree(result.getResponse().getContentAsString());
            Session created = new Session(body.get("accessToken").asText(), body.get("user").get("id").asText());
            SESSIONS.put(email, created);
            return created;
        }
    }

    private record Session(String token, String userId) {
    }
}
