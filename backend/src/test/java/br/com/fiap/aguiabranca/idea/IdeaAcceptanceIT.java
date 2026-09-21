package br.com.fiap.aguiabranca.idea;

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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.fiap.aguiabranca.AbstractMongoIT;
import br.com.fiap.aguiabranca.guideline.infra.GuidelineDocument;
import br.com.fiap.aguiabranca.user.domain.UserRole;
import br.com.fiap.aguiabranca.user.infra.UserDocument;
import br.com.fiap.aguiabranca.user.infra.UserRepository;

@AutoConfigureMockMvc
class IdeaAcceptanceIT extends AbstractMongoIT {

    private static final String OPERATOR_B_EMAIL = "operador.b@innovatecorp.com";
    private static final Map<String, Session> SESSIONS = new ConcurrentHashMap<>();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Test
    @DisplayName("A-IDEA-01 OPERATOR cria PENDING com authorId do token; LEADER 403; MANAGER cria")
    void aIdea01_operatorCreatesLeaderForbidden() throws Exception {
        Session operator = login("operador@innovatecorp.com", "oper123");
        Session manager = login("gestor@innovatecorp.com", "gest123");
        Session leader = login("lideranca@innovatecorp.com", "lider123");
        String title = "Ideia operador " + UUID.randomUUID();

        JsonNode created = createIdea(operator.token(), title, "TECHNOLOGY", "null");
        assertThat(created.get("status").asText()).isEqualTo("PENDING");
        assertThat(created.get("authorId").asText()).isEqualTo(operator.userId());
        assertThat(created.get("authorName").asText()).isEqualTo("Ana Operadora");
        assertThat(created.get("decisionHistory").get(0).get("status").asText()).isEqualTo("PENDING");
        assertThat(created.get("decisionHistory").get(0).get("actorUserId").asText()).isEqualTo(operator.userId());
        assertThat(created.get("decisionHistory").get(0).get("justification").isNull()).isTrue();

        mockMvc.perform(post("/api/v1/ideas")
                        .header("Authorization", "Bearer " + operator.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ideaBody("ab", "Descrição com tamanho válido para a ideia.", "OTHER", "null")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));

        String leaderTitle = "Líder não cria " + UUID.randomUUID();
        mockMvc.perform(post("/api/v1/ideas")
                        .header("Authorization", "Bearer " + leader.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ideaBody(leaderTitle, "Descrição com tamanho válido para a ideia.", "PROCESS", "null")))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("FORBIDDEN"));

        mockMvc.perform(get("/api/v1/ideas")
                        .header("Authorization", "Bearer " + manager.token())
                        .param("q", leaderTitle))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(0));

        JsonNode managerIdea = createIdea(manager.token(), "Ideia gestor " + UUID.randomUUID(), "PRODUCT", "null");
        assertThat(managerIdea.get("authorId").asText()).isEqualTo(manager.userId());
        assertThat(managerIdea.get("status").asText()).isEqualTo("PENDING");
    }

    @Test
    @DisplayName("A-IDEA-02 OPERATOR só lista as próprias ideias")
    void aIdea02_operatorSeesOnlyOwn() throws Exception {
        Session operatorA = login("operador@innovatecorp.com", "oper123");
        Session operatorB = operatorB();
        String title = "Somente A " + UUID.randomUUID();

        JsonNode created = createIdea(operatorA.token(), title, "PROCESS", "null");
        String id = created.get("id").asText();

        mockMvc.perform(get("/api/v1/ideas")
                        .header("Authorization", "Bearer " + operatorA.token())
                        .param("q", title))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].id").value(id))
                .andExpect(jsonPath("$.content[0].authorId").value(operatorA.userId()));

        mockMvc.perform(get("/api/v1/ideas")
                        .header("Authorization", "Bearer " + operatorA.token())
                        .param("q", title)
                        .param("authorId", operatorB.userId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].id").value(id));

        mockMvc.perform(get("/api/v1/ideas")
                        .header("Authorization", "Bearer " + operatorB.token())
                        .param("q", title))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(0));

        MvcResult ownList = mockMvc.perform(get("/api/v1/ideas")
                        .header("Authorization", "Bearer " + operatorB.token())
                        .param("size", "100"))
                .andExpect(status().isOk())
                .andReturn();
        for (JsonNode item : objectMapper.readTree(ownList.getResponse().getContentAsString()).get("content")) {
            assertThat(item.get("authorId").asText()).isEqualTo(operatorB.userId());
        }
    }

    @Test
    @DisplayName("A-IDEA-03 MANAGER lista ideias de todos os autores")
    void aIdea03_managerSeesAll() throws Exception {
        Session operator = login("operador@innovatecorp.com", "oper123");
        Session manager = login("gestor@innovatecorp.com", "gest123");
        String prefix = "Lote " + UUID.randomUUID();

        String operatorIdea = createIdea(operator.token(), prefix + " operador", "TECHNOLOGY", "null").get("id").asText();
        String managerIdea = createIdea(manager.token(), prefix + " gestor", "SUSTAINABILITY", "null").get("id").asText();

        mockMvc.perform(get("/api/v1/ideas")
                        .header("Authorization", "Bearer " + manager.token())
                        .param("q", prefix)
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.content[?(@.id == '" + operatorIdea + "')]").exists())
                .andExpect(jsonPath("$.content[?(@.id == '" + managerIdea + "')]").exists());

        mockMvc.perform(get("/api/v1/ideas/" + operatorIdea)
                        .header("Authorization", "Bearer " + manager.token()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.authorId").value(operator.userId()));
    }

    @Test
    @DisplayName("A-IDEA-04 curadoria: approve, reject com justificativa, 400, 403 e 409")
    void aIdea04_statusMachine() throws Exception {
        Session operator = login("operador@innovatecorp.com", "oper123");
        Session manager = login("gestor@innovatecorp.com", "gest123");
        String ideaId = createIdea(operator.token(), "Curadoria " + UUID.randomUUID(), "OTHER", "null").get("id").asText();

        mockMvc.perform(patch("/api/v1/ideas/" + ideaId + "/status")
                        .header("Authorization", "Bearer " + manager.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(statusBody("APPROVED", "null")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"))
                .andExpect(jsonPath("$.decisionHistory.length()").value(2))
                .andExpect(jsonPath("$.decisionHistory[0].status").value("PENDING"))
                .andExpect(jsonPath("$.decisionHistory[1].status").value("APPROVED"))
                .andExpect(jsonPath("$.decisionHistory[1].actorName").value("Carlos Gestor"));

        mockMvc.perform(patch("/api/v1/ideas/" + ideaId + "/status")
                        .header("Authorization", "Bearer " + manager.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(statusBody("REJECTED", "null")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));

        mockMvc.perform(patch("/api/v1/ideas/" + ideaId + "/status")
                        .header("Authorization", "Bearer " + manager.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(statusBody("REJECTED", "\"curta\"")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));

        mockMvc.perform(get("/api/v1/ideas/" + ideaId)
                        .header("Authorization", "Bearer " + manager.token()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"))
                .andExpect(jsonPath("$.decisionHistory.length()").value(2));

        mockMvc.perform(patch("/api/v1/ideas/" + ideaId + "/status")
                        .header("Authorization", "Bearer " + manager.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(statusBody("REJECTED", "\"Fora da diretriz deste semestre.\"")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("REJECTED"))
                .andExpect(jsonPath("$.decisionHistory.length()").value(3))
                .andExpect(jsonPath("$.decisionHistory[2].status").value("REJECTED"))
                .andExpect(jsonPath("$.decisionHistory[2].justification").value("Fora da diretriz deste semestre."));

        mockMvc.perform(patch("/api/v1/ideas/" + ideaId + "/status")
                        .header("Authorization", "Bearer " + manager.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(statusBody("APPROVED", "null")))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("INVALID_STATUS_TRANSITION"));

        String stillPending = createIdea(operator.token(), "Ainda pendente " + UUID.randomUUID(), "PROCESS", "null").get("id").asText();
        mockMvc.perform(patch("/api/v1/ideas/" + stillPending + "/status")
                        .header("Authorization", "Bearer " + manager.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(statusBody("PENDING", "null")))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("INVALID_STATUS_TRANSITION"));

        mockMvc.perform(patch("/api/v1/ideas/" + stillPending + "/status")
                        .header("Authorization", "Bearer " + operator.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(statusBody("APPROVED", "null")))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("FORBIDDEN"));

        mockMvc.perform(get("/api/v1/ideas/" + stillPending)
                        .header("Authorization", "Bearer " + manager.token()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PENDING"));

        String toPrioritize = createIdea(operator.token(), "Priorizar " + UUID.randomUUID(), "PRODUCT", "null").get("id").asText();
        mockMvc.perform(patch("/api/v1/ideas/" + toPrioritize + "/status")
                        .header("Authorization", "Bearer " + manager.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(statusBody("PRIORITIZED", "null")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PRIORITIZED"))
                .andExpect(jsonPath("$.decisionHistory[1].status").value("PRIORITIZED"));
    }

    @Test
    @DisplayName("A-IDEA-05 sem guidelineId usa a mais recente; UUID inexistente 422")
    void aIdea05_guidelineBinding() throws Exception {
        Session operator = login("operador@innovatecorp.com", "oper123");
        Session leader = login("lideranca@innovatecorp.com", "lider123");
        String olderId = createGuideline(leader.token(), "Eixo antigo " + UUID.randomUUID());
        String newerId = createGuideline(leader.token(), "Eixo vigente " + UUID.randomUUID());
        mongoTemplate.updateFirst(
                Query.query(Criteria.where("_id").is(olderId)),
                new Update().set("updatedAt", Instant.parse("2020-01-01T00:00:00Z")),
                GuidelineDocument.class
        );

        JsonNode auto = createIdea(operator.token(), "Sem diretriz " + UUID.randomUUID(), "SUSTAINABILITY", "null");
        assertThat(auto.get("guidelineId").asText()).isEqualTo(newerId);
        assertThat(auto.get("guidelineTitle").asText()).startsWith("Eixo vigente");

        JsonNode explicit = createIdea(
                operator.token(),
                "Diretriz explícita " + UUID.randomUUID(),
                "PROCESS",
                "\"" + olderId + "\""
        );
        assertThat(explicit.get("guidelineId").asText()).isEqualTo(olderId);

        mockMvc.perform(post("/api/v1/ideas")
                        .header("Authorization", "Bearer " + operator.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ideaBody(
                                "Diretriz fantasma " + UUID.randomUUID(),
                                "Descrição com tamanho válido para a ideia.",
                                "OTHER",
                                "\"" + UUID.randomUUID() + "\""
                        )))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.code").value("BUSINESS_RULE_VIOLATION"));
    }

    @Test
    @DisplayName("A-SEC-03 OPERATOR lendo ideia de outro recebe 404")
    void aSec03_operatorCannotReadOthersIdea() throws Exception {
        Session operator = login("operador@innovatecorp.com", "oper123");
        Session manager = login("gestor@innovatecorp.com", "gest123");
        String id = createIdea(manager.token(), "Ideia do gestor " + UUID.randomUUID(), "TECHNOLOGY", "null").get("id").asText();

        mockMvc.perform(get("/api/v1/ideas/" + id)
                        .header("Authorization", "Bearer " + operator.token()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("NOT_FOUND"));

        mockMvc.perform(get("/api/v1/ideas/" + id)
                        .header("Authorization", "Bearer " + manager.token()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id));

        mockMvc.perform(get("/api/v1/ideas/" + UUID.randomUUID())
                        .header("Authorization", "Bearer " + manager.token()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("NOT_FOUND"));
    }

    @Test
    @DisplayName("A-SEC-04 LEADER não altera status da ideia")
    void aSec04_leaderCannotPatchStatus() throws Exception {
        Session operator = login("operador@innovatecorp.com", "oper123");
        Session leader = login("lideranca@innovatecorp.com", "lider123");
        Session manager = login("gestor@innovatecorp.com", "gest123");
        String id = createIdea(operator.token(), "Líder não decide " + UUID.randomUUID(), "OTHER", "null").get("id").asText();

        mockMvc.perform(patch("/api/v1/ideas/" + id + "/status")
                        .header("Authorization", "Bearer " + leader.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(statusBody("APPROVED", "null")))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("FORBIDDEN"));

        mockMvc.perform(get("/api/v1/ideas/" + id)
                        .header("Authorization", "Bearer " + manager.token()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.decisionHistory.length()").value(1));
    }

    @Test
    @DisplayName("Listagem aceita paginação e filtros da spec")
    void listFilters_paginationStatusCategoryMineAndPeriod() throws Exception {
        Session operator = login("operador@innovatecorp.com", "oper123");
        Session manager = login("gestor@innovatecorp.com", "gest123");
        Session leader = login("lideranca@innovatecorp.com", "lider123");
        String prefix = "Filtro " + UUID.randomUUID();

        String pendingTech = createIdea(operator.token(), prefix + " tech", "TECHNOLOGY", "null").get("id").asText();
        String approved = createIdea(manager.token(), prefix + " produto", "PRODUCT", "null").get("id").asText();
        mockMvc.perform(patch("/api/v1/ideas/" + approved + "/status")
                        .header("Authorization", "Bearer " + manager.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(statusBody("APPROVED", "null")))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/ideas")
                        .header("Authorization", "Bearer " + manager.token())
                        .param("q", prefix)
                        .param("status", "PENDING")
                        .param("category", "TECHNOLOGY"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].id").value(pendingTech));

        mockMvc.perform(get("/api/v1/ideas")
                        .header("Authorization", "Bearer " + manager.token())
                        .param("q", prefix)
                        .param("mine", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].id").value(approved));

        mockMvc.perform(get("/api/v1/ideas")
                        .header("Authorization", "Bearer " + leader.token())
                        .param("q", prefix)
                        .param("mine", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(2));

        Instant from = Instant.now().minus(1, ChronoUnit.DAYS);
        Instant to = Instant.now().plus(1, ChronoUnit.DAYS);
        mockMvc.perform(get("/api/v1/ideas")
                        .header("Authorization", "Bearer " + manager.token())
                        .param("q", prefix)
                        .param("from", from.toString())
                        .param("to", to.toString())
                        .param("page", "0")
                        .param("size", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(1))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.totalPages").value(2))
                .andExpect(jsonPath("$.content.length()").value(1));

        mockMvc.perform(get("/api/v1/ideas")
                        .header("Authorization", "Bearer " + manager.token())
                        .param("sort", "password"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    @Test
    @DisplayName("A-IDEA-06 OPERATOR edita e exclui a própria ideia PENDING; 403, 404, 409 e 422")
    void aIdea06_operatorUpdatesAndDeletesOwnPendingIdea() throws Exception {
        Session operator = login("operador@innovatecorp.com", "oper123");
        Session operatorOther = operatorB();
        Session manager = login("gestor@innovatecorp.com", "gest123");
        Session leader = login("lideranca@innovatecorp.com", "lider123");
        String guidelineId = createGuideline(leader.token(), "Eixo da edição " + UUID.randomUUID());
        String ideaId = createIdea(operator.token(), "Editável " + UUID.randomUUID(), "PROCESS", "null").get("id").asText();

        mockMvc.perform(put("/api/v1/ideas/" + ideaId)
                        .header("Authorization", "Bearer " + operator.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ideaBody(
                                "Título revisado da ideia",
                                "Descrição revisada com tamanho válido para a ideia.",
                                "SUSTAINABILITY",
                                "\"" + guidelineId + "\""
                        )))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Título revisado da ideia"))
                .andExpect(jsonPath("$.category").value("SUSTAINABILITY"))
                .andExpect(jsonPath("$.guidelineId").value(guidelineId))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.authorId").value(operator.userId()));

        mockMvc.perform(put("/api/v1/ideas/" + ideaId)
                        .header("Authorization", "Bearer " + operatorOther.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ideaBody("Outro autor", "Descrição com tamanho válido para a ideia.", "OTHER", "null")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("NOT_FOUND"));

        mockMvc.perform(delete("/api/v1/ideas/" + ideaId)
                        .header("Authorization", "Bearer " + operatorOther.token()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("NOT_FOUND"));

        mockMvc.perform(put("/api/v1/ideas/" + ideaId)
                        .header("Authorization", "Bearer " + manager.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ideaBody("Gestor edita", "Descrição com tamanho válido para a ideia.", "OTHER", "null")))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("FORBIDDEN"));

        mockMvc.perform(delete("/api/v1/ideas/" + ideaId)
                        .header("Authorization", "Bearer " + leader.token()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("FORBIDDEN"));

        mockMvc.perform(put("/api/v1/ideas/" + ideaId)
                        .header("Authorization", "Bearer " + operator.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ideaBody(
                                "Diretriz fantasma",
                                "Descrição com tamanho válido para a ideia.",
                                "OTHER",
                                "\"" + UUID.randomUUID() + "\""
                        )))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.code").value("BUSINESS_RULE_VIOLATION"));

        String approvedId = createIdea(operator.token(), "Já aprovada " + UUID.randomUUID(), "PRODUCT", "null").get("id").asText();
        mockMvc.perform(patch("/api/v1/ideas/" + approvedId + "/status")
                        .header("Authorization", "Bearer " + manager.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(statusBody("APPROVED", "null")))
                .andExpect(status().isOk());

        mockMvc.perform(put("/api/v1/ideas/" + approvedId)
                        .header("Authorization", "Bearer " + operator.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ideaBody("Não edita", "Descrição com tamanho válido para a ideia.", "OTHER", "null")))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.code").value("BUSINESS_RULE_VIOLATION"));

        mockMvc.perform(post("/api/v1/projects")
                        .header("Authorization", "Bearer " + manager.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"ideaId\":\"" + approvedId + "\"}"))
                .andExpect(status().isCreated());

        mockMvc.perform(delete("/api/v1/ideas/" + approvedId)
                        .header("Authorization", "Bearer " + operator.token()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("IDEA_ALREADY_HAS_PROJECT"));

        mockMvc.perform(delete("/api/v1/ideas/" + ideaId)
                        .header("Authorization", "Bearer " + operator.token()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/ideas/" + ideaId)
                        .header("Authorization", "Bearer " + operator.token()))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("OpenAPI publica PUT e DELETE da ideia")
    void openApi_exposesIdeaRoutes() throws Exception {
        MvcResult docs = mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode paths = objectMapper.readTree(docs.getResponse().getContentAsString()).get("paths");
        JsonNode collection = paths.get("/api/v1/ideas");
        JsonNode item = paths.get("/api/v1/ideas/{id}");
        JsonNode statusPath = paths.get("/api/v1/ideas/{id}/status");
        assertThat(collection.get("get")).isNotNull();
        assertThat(collection.get("post")).isNotNull();
        assertThat(collection.has("put")).isFalse();
        assertThat(collection.has("delete")).isFalse();
        assertThat(item.get("get")).isNotNull();
        assertThat(item.get("put")).isNotNull();
        assertThat(item.get("delete")).isNotNull();
        assertThat(statusPath.get("patch")).isNotNull();
    }

    private JsonNode createIdea(String token, String title, String category, String guidelineIdJson) throws Exception {
        MvcResult created = mockMvc.perform(post("/api/v1/ideas")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ideaBody(title, "Descrição com tamanho válido para a ideia.", category, guidelineIdJson)))
                .andExpect(status().isCreated())
                .andReturn();
        return objectMapper.readTree(created.getResponse().getContentAsString());
    }

    private String ideaBody(String title, String description, String category, String guidelineIdJson) {
        return """
                {"title":"%s","description":"%s","category":"%s","guidelineId":%s,"authorId":"bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb"}
                """.formatted(title, description, category, guidelineIdJson);
    }

    private String statusBody(String status, String justificationJson) {
        return """
                {"status":"%s","justification":%s}
                """.formatted(status, justificationJson);
    }

    private String createGuideline(String leaderToken, String title) throws Exception {
        MvcResult created = mockMvc.perform(post("/api/v1/guidelines")
                        .header("Authorization", "Bearer " + leaderToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"%s","content":"Conteúdo mínimo válido para a diretriz.","category":null,"campaign":null}
                                """.formatted(title)))
                .andExpect(status().isCreated())
                .andReturn();
        return objectMapper.readTree(created.getResponse().getContentAsString()).get("id").asText();
    }

    private Session operatorB() throws Exception {
        if (userRepository.findByEmail(OPERATOR_B_EMAIL).isEmpty()) {
            UserDocument user = new UserDocument();
            user.setId(UUID.randomUUID().toString());
            user.setName("Bruno Operador");
            user.setEmail(OPERATOR_B_EMAIL);
            user.setPasswordHash(passwordEncoder.encode("oper123"));
            user.setRole(UserRole.OPERATOR);
            user.setActive(true);
            userRepository.save(user);
        }
        return login(OPERATOR_B_EMAIL, "oper123");
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
            Session created = authenticate(email, password);
            SESSIONS.put(email, created);
            return created;
        }
    }

    private Session authenticate(String email, String password) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"%s","password":"%s"}
                                """.formatted(email, password)))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode body = objectMapper.readTree(result.getResponse().getContentAsString());
        return new Session(
                body.get("accessToken").asText(),
                body.get("user").get("id").asText(),
                body.get("user").get("name").asText()
        );
    }

    private record Session(String token, String userId, String name) {
    }
}
