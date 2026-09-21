package br.com.fiap.aguiabranca.project;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bson.Document;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.fiap.aguiabranca.AbstractMongoIT;

@AutoConfigureMockMvc
class ProjectAcceptanceIT extends AbstractMongoIT {

    private static final Map<String, Session> SESSIONS = new ConcurrentHashMap<>();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Test
    @DisplayName("A-PROJ-01 gestor cria BACKLOG copiando a ideia; segundo POST 409; PENDING 422; líder e operador 403")
    void aProj01_createFromApprovedIdea() throws Exception {
        Session manager = login("gestor@innovatecorp.com", "gest123");
        Session leader = login("lideranca@innovatecorp.com", "lider123");
        Session operator = login("operador@innovatecorp.com", "oper123");

        String title = "Fila express " + UUID.randomUUID();
        JsonNode idea = createIdea(operator.token(), title);
        approve(manager.token(), idea.get("id").asText(), "APPROVED");

        mockMvc.perform(post("/api/v1/projects")
                        .header("Authorization", "Bearer " + leader.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(projectBody(idea.get("id").asText())))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("FORBIDDEN"));

        mockMvc.perform(post("/api/v1/projects")
                        .header("Authorization", "Bearer " + operator.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(projectBody(idea.get("id").asText())))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("FORBIDDEN"));

        JsonNode created = createProject(manager.token(), idea.get("id").asText());
        assertThat(created.get("status").asText()).isEqualTo("BACKLOG");
        assertThat(created.get("title").asText()).isEqualTo(idea.get("title").asText());
        assertThat(created.get("description").asText()).isEqualTo(idea.get("description").asText());
        assertThat(created.get("guidelineId").asText()).isEqualTo(idea.get("guidelineId").asText());
        assertThat(created.get("guidelineTitle").asText()).isEqualTo(idea.get("guidelineTitle").asText());
        assertThat(created.get("ideaId").asText()).isEqualTo(idea.get("id").asText());
        assertThat(created.get("managerId").asText()).isEqualTo(manager.userId());
        assertThat(created.get("managerName").asText()).isEqualTo("Carlos Gestor");
        assertThat(created.get("investmentAmount").asDouble()).isEqualTo(0.0);
        assertThat(created.get("obtainedProfit").asDouble()).isEqualTo(0.0);
        assertThat(created.get("productivityGainPercent").asDouble()).isEqualTo(0.0);
        assertThat(created.get("roiPercent").asDouble()).isEqualTo(0.0);
        assertThat(created.get("deadline").isNull()).isTrue();

        mockMvc.perform(post("/api/v1/projects")
                        .header("Authorization", "Bearer " + manager.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(projectBody(idea.get("id").asText())))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("IDEA_ALREADY_HAS_PROJECT"));

        String pendingId = createIdea(operator.token(), "Ainda pendente " + UUID.randomUUID()).get("id").asText();
        mockMvc.perform(post("/api/v1/projects")
                        .header("Authorization", "Bearer " + manager.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(projectBody(pendingId)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.code").value("BUSINESS_RULE_VIOLATION"));

        mockMvc.perform(get("/api/v1/projects/by-idea/" + pendingId)
                        .header("Authorization", "Bearer " + manager.token()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("NOT_FOUND"));

        JsonNode prioritizedIdea = createIdea(operator.token(), "Priorizada " + UUID.randomUUID());
        approve(manager.token(), prioritizedIdea.get("id").asText(), "PRIORITIZED");
        JsonNode fromPrioritized = createProject(manager.token(), prioritizedIdea.get("id").asText());
        assertThat(fromPrioritized.get("status").asText()).isEqualTo("BACKLOG");
        assertThat(fromPrioritized.get("title").asText()).isEqualTo(prioritizedIdea.get("title").asText());

        mockMvc.perform(get("/api/v1/projects/" + created.get("id").asText())
                        .header("Authorization", "Bearer " + leader.token()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(created.get("id").asText()))
                .andExpect(jsonPath("$.status").value("BACKLOG"));

        mockMvc.perform(get("/api/v1/projects/by-idea/" + idea.get("id").asText())
                        .header("Authorization", "Bearer " + leader.token()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(created.get("id").asText()));

        mockMvc.perform(post("/api/v1/projects")
                        .header("Authorization", "Bearer " + manager.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(projectBody(UUID.randomUUID().toString())))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("NOT_FOUND"));
    }

    @Test
    @DisplayName("A-PROJ-02 PUT grava investimento e devolve roiPercent 50; produtividade 101 é 400")
    void aProj02_updateFinancialsAndRoi() throws Exception {
        Session manager = login("gestor@innovatecorp.com", "gest123");
        Session leader = login("lideranca@innovatecorp.com", "lider123");
        Session operator = login("operador@innovatecorp.com", "oper123");
        JsonNode project = approvedProject(manager, operator, "ROI cinquenta " + UUID.randomUUID());
        String projectId = project.get("id").asText();
        String ideaId = project.get("ideaId").asText();
        String createdAt = project.get("createdAt").asText();
        String guidelineId = project.get("guidelineId").asText();

        mockMvc.perform(put("/api/v1/projects/" + projectId)
                        .header("Authorization", "Bearer " + leader.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateBody(project.get("title").asText(), "IN_DEVELOPMENT", "1000", "1500", "10", "\"" + guidelineId + "\"")))
                .andExpect(status().isForbidden());

        mockMvc.perform(put("/api/v1/projects/" + projectId)
                        .header("Authorization", "Bearer " + operator.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateBody(project.get("title").asText(), "IN_DEVELOPMENT", "1000", "1500", "10", "\"" + guidelineId + "\"")))
                .andExpect(status().isForbidden());

        JsonNode updated = read(mockMvc.perform(put("/api/v1/projects/" + projectId)
                        .header("Authorization", "Bearer " + manager.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateBody(project.get("title").asText(), "IN_DEVELOPMENT", "1000", "1500", "10", "\"" + guidelineId + "\"")))
                .andExpect(status().isOk())
                .andReturn());

        assertThat(updated.get("roiPercent").asDouble()).isEqualTo(50.0);
        assertThat(updated.get("investmentAmount").asDouble()).isEqualTo(1000.0);
        assertThat(updated.get("obtainedProfit").asDouble()).isEqualTo(1500.0);
        assertThat(updated.get("productivityGainPercent").asDouble()).isEqualTo(10.0);
        assertThat(updated.get("status").asText()).isEqualTo("IN_DEVELOPMENT");
        assertThat(updated.get("deadline").asText()).isEqualTo("2026-12-31T00:00:00Z");
        assertThat(updated.get("ideaId").asText()).isEqualTo(ideaId);
        assertThat(updated.get("managerId").asText()).isEqualTo(manager.userId());
        assertThat(Instant.parse(updated.get("createdAt").asText()).truncatedTo(ChronoUnit.MILLIS))
                .isEqualTo(Instant.parse(createdAt).truncatedTo(ChronoUnit.MILLIS));

        Document stored = mongoTemplate.findById(projectId, Document.class, "projects");
        assertThat(stored).isNotNull();
        assertThat(stored.containsKey("roiPercent")).isFalse();

        mockMvc.perform(put("/api/v1/projects/" + projectId)
                        .header("Authorization", "Bearer " + manager.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateBody(project.get("title").asText(), "IN_DEVELOPMENT", "1000", "1500", "101", "\"" + guidelineId + "\"")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));

        mockMvc.perform(put("/api/v1/projects/" + projectId)
                        .header("Authorization", "Bearer " + manager.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateBody(project.get("title").asText(), "IN_DEVELOPMENT", "-1", "1500", "10", "\"" + guidelineId + "\"")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));

        mockMvc.perform(put("/api/v1/projects/" + projectId)
                        .header("Authorization", "Bearer " + manager.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateBody(project.get("title").asText(), "IN_DEVELOPMENT", "1000", "-5", "10", "\"" + guidelineId + "\"")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));

        mockMvc.perform(put("/api/v1/projects/" + projectId)
                        .header("Authorization", "Bearer " + manager.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateBody(project.get("title").asText(), "COMPLETED", "1000", "1500", "10", "\"" + UUID.randomUUID() + "\"")))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.code").value("BUSINESS_RULE_VIOLATION"));

        mockMvc.perform(get("/api/v1/projects/" + projectId)
                        .header("Authorization", "Bearer " + manager.token()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.investmentAmount").value(1000.0))
                .andExpect(jsonPath("$.status").value("IN_DEVELOPMENT"))
                .andExpect(jsonPath("$.roiPercent").value(50.0))
                .andExpect(jsonPath("$.guidelineId").value(guidelineId));
    }

    @Test
    @DisplayName("A-PROJ-03 DELETE do gestor remove o projeto e preserva a ideia; líder 403")
    void aProj03_deleteKeepsIdea() throws Exception {
        Session manager = login("gestor@innovatecorp.com", "gest123");
        Session leader = login("lideranca@innovatecorp.com", "lider123");
        Session operator = login("operador@innovatecorp.com", "oper123");
        JsonNode project = approvedProject(manager, operator, "Excluir projeto " + UUID.randomUUID());
        String projectId = project.get("id").asText();
        String ideaId = project.get("ideaId").asText();

        mockMvc.perform(delete("/api/v1/projects/" + projectId)
                        .header("Authorization", "Bearer " + leader.token()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("FORBIDDEN"));

        mockMvc.perform(delete("/api/v1/projects/" + projectId)
                        .header("Authorization", "Bearer " + operator.token()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("FORBIDDEN"));

        mockMvc.perform(get("/api/v1/ideas/" + ideaId)
                        .header("Authorization", "Bearer " + manager.token()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));

        mockMvc.perform(delete("/api/v1/projects/" + projectId)
                        .header("Authorization", "Bearer " + manager.token()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/projects/" + projectId)
                        .header("Authorization", "Bearer " + manager.token()))
                .andExpect(status().isNotFound());

        mockMvc.perform(get("/api/v1/projects/by-idea/" + ideaId)
                        .header("Authorization", "Bearer " + manager.token()))
                .andExpect(status().isNotFound());

        mockMvc.perform(get("/api/v1/ideas/" + ideaId)
                        .header("Authorization", "Bearer " + manager.token()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ideaId))
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    @DisplayName("A-PROJ-04 operador não lista projetos; líder lista")
    void aProj04_leaderListsOperatorForbidden() throws Exception {
        Session manager = login("gestor@innovatecorp.com", "gest123");
        Session leader = login("lideranca@innovatecorp.com", "lider123");
        Session operator = login("operador@innovatecorp.com", "oper123");
        String title = "Lista liderança " + UUID.randomUUID();
        JsonNode project = approvedProject(manager, operator, title);

        mockMvc.perform(get("/api/v1/projects")
                        .header("Authorization", "Bearer " + operator.token()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("FORBIDDEN"));

        mockMvc.perform(get("/api/v1/projects/" + project.get("id").asText())
                        .header("Authorization", "Bearer " + operator.token()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("FORBIDDEN"));

        mockMvc.perform(get("/api/v1/projects/by-idea/" + project.get("ideaId").asText())
                        .header("Authorization", "Bearer " + operator.token()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("FORBIDDEN"));

        mockMvc.perform(get("/api/v1/projects")
                        .header("Authorization", "Bearer " + leader.token())
                        .param("q", title)
                        .param("status", "BACKLOG"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].id").value(project.get("id").asText()))
                .andExpect(jsonPath("$.content[0].managerName").value("Carlos Gestor"));

        mockMvc.perform(get("/api/v1/projects")
                        .header("Authorization", "Bearer " + manager.token())
                        .param("q", title)
                        .param("status", "COMPLETED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(0));
    }

    @Test
    @DisplayName("A-CALC-01 ROI do item: investimento 0 devolve 0; 200 e lucro 100 devolvem -50")
    void aCalc01_itemRoi() throws Exception {
        Session manager = login("gestor@innovatecorp.com", "gest123");
        Session operator = login("operador@innovatecorp.com", "oper123");
        JsonNode project = approvedProject(manager, operator, "ROI negativo " + UUID.randomUUID());
        String projectId = project.get("id").asText();
        String guidelineId = project.get("guidelineId").isNull() ? "null" : "\"" + project.get("guidelineId").asText() + "\"";

        mockMvc.perform(get("/api/v1/projects/" + projectId)
                        .header("Authorization", "Bearer " + manager.token()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.investmentAmount").value(0.0))
                .andExpect(jsonPath("$.roiPercent").value(0.0));

        mockMvc.perform(put("/api/v1/projects/" + projectId)
                        .header("Authorization", "Bearer " + manager.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateBody(project.get("title").asText(), "BACKLOG", "0", "100", "0", guidelineId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.roiPercent").value(0.0));

        JsonNode negative = read(mockMvc.perform(put("/api/v1/projects/" + projectId)
                        .header("Authorization", "Bearer " + manager.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateBody(project.get("title").asText(), "AVERAGE_TICKET", "200", "100", "0", guidelineId)))
                .andExpect(status().isOk())
                .andReturn());
        assertThat(negative.get("roiPercent").asDouble()).isEqualTo(-50.0);

        mockMvc.perform(get("/api/v1/projects/" + projectId)
                        .header("Authorization", "Bearer " + manager.token()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.investmentAmount").value(200.0))
                .andExpect(jsonPath("$.obtainedProfit").value(100.0))
                .andExpect(jsonPath("$.roiPercent").value(-50.0));

        Document stored = mongoTemplate.findById(projectId, Document.class, "projects");
        assertThat(stored).isNotNull();
        assertThat(stored.containsKey("roiPercent")).isFalse();
        assertThat(stored.get("status")).isEqualTo("AVERAGE_TICKET");
    }

    @Test
    @DisplayName("Ideia com projeto não pode ser reprovada")
    void rejectIdeaThatAlreadyHasProjectIsConflict() throws Exception {
        Session manager = login("gestor@innovatecorp.com", "gest123");
        Session operator = login("operador@innovatecorp.com", "oper123");
        JsonNode project = approvedProject(manager, operator, "Não reprovar " + UUID.randomUUID());
        String ideaId = project.get("ideaId").asText();

        mockMvc.perform(patch("/api/v1/ideas/" + ideaId + "/status")
                        .header("Authorization", "Bearer " + manager.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(statusBody("REJECTED", "\"Já virou projeto e não pode ser reprovada.\"")))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("IDEA_ALREADY_HAS_PROJECT"));

        mockMvc.perform(get("/api/v1/ideas/" + ideaId)
                        .header("Authorization", "Bearer " + manager.token()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));

        mockMvc.perform(patch("/api/v1/ideas/" + ideaId + "/status")
                        .header("Authorization", "Bearer " + manager.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(statusBody("PRIORITIZED", "null")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PRIORITIZED"));
    }

    @Test
    @DisplayName("OpenAPI publica o CRUD de projetos e não cria relatório em /dashboard/projects")
    void openApi_exposesProjectRoutes() throws Exception {
        MvcResult docs = mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode paths = objectMapper.readTree(docs.getResponse().getContentAsString()).get("paths");
        assertThat(paths.get("/api/v1/projects").get("get")).isNotNull();
        assertThat(paths.get("/api/v1/projects").get("post")).isNotNull();
        assertThat(paths.get("/api/v1/projects/{id}").get("get")).isNotNull();
        assertThat(paths.get("/api/v1/projects/{id}").get("put")).isNotNull();
        assertThat(paths.get("/api/v1/projects/{id}").get("delete")).isNotNull();
        assertThat(paths.get("/api/v1/projects/by-idea/{ideaId}").get("get")).isNotNull();
        assertThat(paths.has("/api/v1/dashboard/projects/{id}")).isFalse();
        assertThat(paths.has("/dashboard/projects/{id}")).isFalse();
    }

    private JsonNode approvedProject(Session manager, Session operator, String title) throws Exception {
        JsonNode idea = createIdea(operator.token(), title);
        approve(manager.token(), idea.get("id").asText(), "APPROVED");
        return createProject(manager.token(), idea.get("id").asText());
    }

    private JsonNode createProject(String token, String ideaId) throws Exception {
        return read(mockMvc.perform(post("/api/v1/projects")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(projectBody(ideaId)))
                .andExpect(status().isCreated())
                .andReturn());
    }

    private JsonNode createIdea(String token, String title) throws Exception {
        return read(mockMvc.perform(post("/api/v1/ideas")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"%s","description":"Descrição com tamanho válido para a ideia.","category":"TECHNOLOGY","guidelineId":null}
                                """.formatted(title)))
                .andExpect(status().isCreated())
                .andReturn());
    }

    private void approve(String token, String ideaId, String status) throws Exception {
        mockMvc.perform(patch("/api/v1/ideas/" + ideaId + "/status")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(statusBody(status, "null")))
                .andExpect(status().isOk());
    }

    private String projectBody(String ideaId) {
        return """
                {"ideaId":"%s"}
                """.formatted(ideaId);
    }

    private String updateBody(
            String title,
            String status,
            String investment,
            String profit,
            String productivity,
            String guidelineIdJson
    ) {
        return """
                {"title":"%s","description":"Descrição atualizada do projeto com tamanho válido.","status":"%s","investmentAmount":%s,"obtainedProfit":%s,"productivityGainPercent":%s,"deadline":"2026-12-31T00:00:00Z","guidelineId":%s,"ideaId":"ignorado","managerId":"ignorado"}
                """.formatted(title, status, investment, profit, productivity, guidelineIdJson);
    }

    private String statusBody(String status, String justificationJson) {
        return """
                {"status":"%s","justification":%s}
                """.formatted(status, justificationJson);
    }

    private JsonNode read(MvcResult result) throws Exception {
        return objectMapper.readTree(result.getResponse().getContentAsString());
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
