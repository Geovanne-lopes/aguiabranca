package br.com.fiap.aguiabranca.dashboard;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bson.Document;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.fiap.aguiabranca.AbstractMongoIT;

@AutoConfigureMockMvc
class DashboardAcceptanceIT extends AbstractMongoIT {

    private static final Map<String, Session> SESSIONS = new ConcurrentHashMap<>();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Test
    @DisplayName("A-DASH-01 operador vê só as próprias ideias; gestor e líder 403")
    void aDash01_operatorCountsOwnIdeas() throws Exception {
        String suffix = UUID.randomUUID().toString().substring(0, 8);
        Session operator = register("Olívia " + suffix, "olivia." + suffix + "@innovatecorp.com", "OPERATOR");
        Session manager = login("gestor@innovatecorp.com", "gest123");
        Session leader = login("lideranca@innovatecorp.com", "lider123");

        mockMvc.perform(get("/api/v1/dashboard/operator").header("Authorization", "Bearer " + manager.token()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("FORBIDDEN"));
        mockMvc.perform(get("/api/v1/dashboard/operator").header("Authorization", "Bearer " + leader.token()))
                .andExpect(status().isForbidden());

        String first = createIdea(operator.token(), "Ideia um " + suffix).get("id").asText();
        createIdea(operator.token(), "Ideia dois " + suffix);
        approve(manager.token(), first);

        JsonNode dashboard = read(mockMvc.perform(get("/api/v1/dashboard/operator")
                        .header("Authorization", "Bearer " + operator.token()))
                .andExpect(status().isOk())
                .andReturn());

        assertThat(dashboard.get("ideasSubmittedCount").asInt()).isEqualTo(2);
        assertThat(dashboard.get("ideasApprovedCount").asInt()).isEqualTo(1);
        assertThat(dashboard.get("ideasPendingCount").asInt()).isEqualTo(1);
        assertThat(dashboard.get("ideasRejectedCount").asInt()).isZero();
        assertThat(dashboard.get("submittedThisMonth").asInt()).isEqualTo(2);
        assertThat(dashboard.get("submittedPreviousMonth").asInt()).isZero();
        assertThat(dashboard.get("submittedTrendLabel").asText()).isEqualTo("Primeiro registro neste mês");
        assertThat(dashboard.get("approvedThisMonth").asInt()).isEqualTo(1);
        assertThat(dashboard.get("approvedTrendLabel").asText()).isEqualTo("Primeiro registro neste mês");
        assertThat(dashboard.get("hasData").asBoolean()).isTrue();
    }

    @Test
    @DisplayName("A-DASH-02 gestor vê barras e pendências; sem ideias os counts zeram e hasData=false")
    void aDash02_managerCountsAndEmptyState() throws Exception {
        Session manager = login("gestor@innovatecorp.com", "gest123");
        Session leader = login("lideranca@innovatecorp.com", "lider123");
        Session operator = login("operador@innovatecorp.com", "oper123");

        mockMvc.perform(get("/api/v1/dashboard/manager").header("Authorization", "Bearer " + operator.token()))
                .andExpect(status().isForbidden());

        JsonNode before = read(mockMvc.perform(get("/api/v1/dashboard/manager")
                        .header("Authorization", "Bearer " + manager.token()))
                .andExpect(status().isOk())
                .andReturn());
        createIdea(operator.token(), "Pendência " + UUID.randomUUID());
        JsonNode after = read(mockMvc.perform(get("/api/v1/dashboard/manager")
                        .header("Authorization", "Bearer " + manager.token()))
                .andExpect(status().isOk())
                .andReturn());

        assertThat(after.get("pendingIdeasCount").asInt()).isEqualTo(before.get("pendingIdeasCount").asInt() + 1);
        assertThat(after.get("monthlyBars")).hasSize(5);
        assertThat(after.get("monthlyBars").get(4).get("count").asInt())
                .isGreaterThanOrEqualTo(before.get("monthlyBars").get(4).get("count").asInt());
        assertThat(after.get("hasData").asBoolean()).isTrue();
        assertThat(after.get("ideasByStatus")).isNotEmpty();

        mockMvc.perform(get("/api/v1/dashboard/manager").header("Authorization", "Bearer " + leader.token()))
                .andExpect(status().isOk());

        mongoTemplate.remove(new Query(), "ideas");
        mongoTemplate.remove(new Query(), "projects");

        JsonNode empty = read(mockMvc.perform(get("/api/v1/dashboard/manager")
                        .header("Authorization", "Bearer " + manager.token()))
                .andExpect(status().isOk())
                .andReturn());
        assertThat(empty.get("pendingIdeasCount").asInt()).isZero();
        assertThat(empty.get("activeProjectsCount").asInt()).isZero();
        assertThat(empty.get("ideasReceivedThisMonth").asInt()).isZero();
        assertThat(empty.get("ideasReceivedPreviousMonth").asInt()).isZero();
        assertThat(empty.get("approvalRatePercent").asInt()).isZero();
        assertThat(empty.get("approvalRateTrendLabel").asText()).isEqualTo("Sem ideias para calcular");
        assertThat(empty.get("ideasReceivedTrendLabel").asText()).isEqualTo("Sem dados no período");
        assertThat(empty.get("hasData").asBoolean()).isFalse();
        assertThat(empty.get("monthlyBars")).hasSize(5);
        for (JsonNode bar : empty.get("monthlyBars")) {
            assertThat(bar.get("count").asInt()).isZero();
        }
    }

    @Test
    @DisplayName("A-DASH-03 ROI consolidado 60 com investimento 100 e lucro 160; operador e gestor 403")
    void aDash03_leaderRoiAndForbiddenRoles() throws Exception {
        Session manager = login("gestor@innovatecorp.com", "gest123");
        Session leader = login("lideranca@innovatecorp.com", "lider123");
        Session operator = login("operador@innovatecorp.com", "oper123");

        mockMvc.perform(get("/api/v1/dashboard/leader").header("Authorization", "Bearer " + operator.token()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("FORBIDDEN"));
        mockMvc.perform(get("/api/v1/dashboard/leader").header("Authorization", "Bearer " + manager.token()))
                .andExpect(status().isForbidden());

        mongoTemplate.remove(new Query(), "projects");
        String suffix = UUID.randomUUID().toString().substring(0, 8);
        JsonNode firstIdea = createIdea(operator.token(), "Projeto A " + suffix);
        JsonNode secondIdea = createIdea(operator.token(), "Projeto B " + suffix);
        approve(manager.token(), firstIdea.get("id").asText());
        approve(manager.token(), secondIdea.get("id").asText());
        JsonNode first = createProject(manager.token(), firstIdea.get("id").asText());
        JsonNode second = createProject(manager.token(), secondIdea.get("id").asText());
        String guidelineId = first.get("guidelineId").isNull() ? "null" : "\"" + first.get("guidelineId").asText() + "\"";
        updateProject(manager.token(), first.get("id").asText(), first.get("title").asText(), "BACKLOG", "100", "150", "0", guidelineId);
        updateProject(manager.token(), second.get("id").asText(), second.get("title").asText(), "COMPLETED", "0", "10", "10", guidelineId);

        JsonNode dashboard = read(mockMvc.perform(get("/api/v1/dashboard/leader")
                        .header("Authorization", "Bearer " + leader.token()))
                .andExpect(status().isOk())
                .andReturn());

        assertThat(dashboard.get("totalInvestment").asDouble()).isEqualTo(100.0);
        assertThat(dashboard.get("totalObtainedProfit").asDouble()).isEqualTo(160.0);
        assertThat(dashboard.get("overallRoiPercent").asDouble()).isEqualTo(60.0);
        assertThat(dashboard.get("totalProjects").asInt()).isEqualTo(2);
        assertThat(dashboard.get("activeProjectsCount").asInt()).isEqualTo(1);
        assertThat(dashboard.get("completedProjectsCount").asInt()).isEqualTo(1);
        assertThat(dashboard.get("averageProductivityGainPercent").asDouble()).isEqualTo(5.0);
        assertThat(dashboard.get("hasData").asBoolean()).isTrue();
        assertThat(dashboard.get("projectsByStatus")).hasSize(5);
        assertThat(countOf(dashboard.get("projectsByStatus"), "BACKLOG")).isEqualTo(1);
        assertThat(countOf(dashboard.get("projectsByStatus"), "COMPLETED")).isEqualTo(1);
        assertThat(countOf(dashboard.get("projectsByStatus"), "IN_DEVELOPMENT")).isZero();
    }

    @Test
    @DisplayName("A-DASH-04 estratégias nomeadas e bucket Sem estratégia para órfãos")
    void aDash04_strategiesIncludeUnassignedBucket() throws Exception {
        Session manager = login("gestor@innovatecorp.com", "gest123");
        Session leader = login("lideranca@innovatecorp.com", "lider123");
        Session operator = login("operador@innovatecorp.com", "oper123");
        String suffix = UUID.randomUUID().toString().substring(0, 8);

        mockMvc.perform(get("/api/v1/dashboard/strategies").header("Authorization", "Bearer " + manager.token()))
                .andExpect(status().isForbidden());

        JsonNode idea = createIdea(operator.token(), "Com diretriz " + suffix);
        approve(manager.token(), idea.get("id").asText());
        JsonNode linked = createProject(manager.token(), idea.get("id").asText());
        String guidelineTitle = linked.get("guidelineTitle").asText();

        JsonNode orphanIdea = createIdea(operator.token(), "Sem diretriz " + suffix);
        approve(manager.token(), orphanIdea.get("id").asText());
        JsonNode orphan = createProject(manager.token(), orphanIdea.get("id").asText());
        updateProject(
                manager.token(),
                orphan.get("id").asText(),
                orphan.get("title").asText(),
                "BACKLOG",
                "0",
                "0",
                "0",
                "null"
        );

        JsonNode strategies = read(mockMvc.perform(get("/api/v1/dashboard/strategies")
                        .header("Authorization", "Bearer " + leader.token()))
                .andExpect(status().isOk())
                .andReturn());

        JsonNode named = findByTitle(strategies.get("items"), guidelineTitle);
        JsonNode unassigned = findByTitle(strategies.get("items"), "Sem estratégia");
        assertThat(named).isNotNull();
        assertThat(named.get("guidelineId").asText()).isEqualTo(linked.get("guidelineId").asText());
        assertThat(named.get("projectsCount").asInt()).isGreaterThanOrEqualTo(1);
        assertThat(unassigned).isNotNull();
        assertThat(unassigned.get("guidelineId").isNull()).isTrue();
        assertThat(unassigned.get("projectsCount").asInt()).isGreaterThanOrEqualTo(1);
    }

    @Test
    @DisplayName("A-DASH-05 período com from posterior a to ou ausente é 400; intervalo vazio é 200")
    void aDash05_periodValidatesRange() throws Exception {
        Session leader = login("lideranca@innovatecorp.com", "lider123");
        Session operator = login("operador@innovatecorp.com", "oper123");

        mockMvc.perform(get("/api/v1/dashboard/period").header("Authorization", "Bearer " + leader.token()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
        mockMvc.perform(get("/api/v1/dashboard/period")
                        .header("Authorization", "Bearer " + leader.token())
                        .param("from", "2026-06-01T00:00:00Z")
                        .param("to", "2026-01-01T00:00:00Z"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
        mockMvc.perform(get("/api/v1/dashboard/period")
                        .header("Authorization", "Bearer " + operator.token())
                        .param("from", "2026-01-01T00:00:00Z")
                        .param("to", "2026-06-01T00:00:00Z"))
                .andExpect(status().isForbidden());

        JsonNode emptyWindow = read(mockMvc.perform(get("/api/v1/dashboard/period")
                        .header("Authorization", "Bearer " + leader.token())
                        .param("from", "2099-01-01T00:00:00Z")
                        .param("to", "2099-01-02T00:00:00Z"))
                .andExpect(status().isOk())
                .andReturn());
        assertThat(emptyWindow.get("hasData").asBoolean()).isFalse();
        assertThat(emptyWindow.get("totalProjects").asInt()).isZero();
        assertThat(emptyWindow.get("overallRoiPercent").asDouble()).isEqualTo(0.0);
        assertThat(emptyWindow.get("from").asText()).isEqualTo("2099-01-01T00:00:00Z");
        assertThat(emptyWindow.get("to").asText()).isEqualTo("2099-01-02T00:00:00Z");
    }

    @Test
    @DisplayName("A-RANK-01 ranking usa nome e e-mail de users, não mock-op")
    void aRank01_rankingsUseRealUsers() throws Exception {
        String suffix = UUID.randomUUID().toString().substring(0, 8);
        Session ana = register("Ana Rank " + suffix, "ana.rank." + suffix + "@innovatecorp.com", "OPERATOR");
        Session zeca = register("Zeca Rank " + suffix, "zeca.rank." + suffix + "@innovatecorp.com", "OPERATOR");
        Session manager = login("gestor@innovatecorp.com", "gest123");
        Session operator = login("operador@innovatecorp.com", "oper123");

        createIdea(ana.token(), "Rank A1 " + suffix);
        JsonNode approved = createIdea(ana.token(), "Rank A2 " + suffix);
        approve(manager.token(), approved.get("id").asText());
        createIdea(zeca.token(), "Rank Z1 " + suffix);

        mockMvc.perform(get("/api/v1/dashboard/rankings").header("Authorization", "Bearer " + operator.token()))
                .andExpect(status().isForbidden());

        JsonNode rankings = read(mockMvc.perform(get("/api/v1/dashboard/rankings")
                        .header("Authorization", "Bearer " + manager.token())
                        .param("period", "ALL"))
                .andExpect(status().isOk())
                .andReturn());

        assertThat(rankings.toString()).doesNotContain("mock-op");
        JsonNode anaRow = findByEmail(rankings.get("items"), ana.email());
        JsonNode zecaRow = findByEmail(rankings.get("items"), zeca.email());
        assertThat(anaRow.get("name").asText()).isEqualTo("Ana Rank " + suffix);
        assertThat(anaRow.get("email").asText()).isEqualTo(ana.email());
        assertThat(anaRow.get("ideasSubmitted").asInt()).isEqualTo(2);
        assertThat(anaRow.get("ideasApproved").asInt()).isEqualTo(1);
        assertThat(zecaRow.get("name").asText()).isEqualTo("Zeca Rank " + suffix);
        assertThat(zecaRow.get("ideasSubmitted").asInt()).isEqualTo(1);

        Document anaUser = mongoTemplate.findOne(
                org.springframework.data.mongodb.core.query.Query.query(
                        org.springframework.data.mongodb.core.query.Criteria.where("email").is(ana.email())
                ),
                Document.class,
                "users"
        );
        assertThat(anaUser).isNotNull();
        assertThat(anaRow.get("name").asText()).isEqualTo(anaUser.getString("name"));
        assertThat(anaRow.get("authorId").asText()).isEqualTo(anaUser.getString("_id"));

        JsonNode month = read(mockMvc.perform(get("/api/v1/dashboard/rankings")
                        .header("Authorization", "Bearer " + manager.token())
                        .param("period", "MONTH"))
                .andExpect(status().isOk())
                .andReturn());
        assertThat(findByEmail(month.get("items"), ana.email()).get("ideasSubmitted").asInt()).isEqualTo(2);
    }

    @Test
    @DisplayName("OpenAPI publica os dashboards e não cria /dashboard/projects/{id}")
    void openApi_exposesDashboardsWithoutProjectAlias() throws Exception {
        JsonNode paths = objectMapper.readTree(mockMvc.perform(get("/v3/api-docs"))
                        .andExpect(status().isOk())
                        .andReturn()
                        .getResponse()
                        .getContentAsString())
                .get("paths");
        assertThat(paths.get("/api/v1/dashboard/operator").get("get")).isNotNull();
        assertThat(paths.get("/api/v1/dashboard/manager").get("get")).isNotNull();
        assertThat(paths.get("/api/v1/dashboard/leader").get("get")).isNotNull();
        assertThat(paths.get("/api/v1/dashboard/strategies").get("get")).isNotNull();
        assertThat(paths.get("/api/v1/dashboard/period").get("get")).isNotNull();
        assertThat(paths.get("/api/v1/dashboard/rankings").get("get")).isNotNull();
        assertThat(paths.get("/api/v1/dashboard/projects/{id}")).isNull();
    }

    private int countOf(JsonNode statuses, String status) {
        for (JsonNode item : statuses) {
            if (status.equals(item.get("status").asText())) {
                return item.get("count").asInt();
            }
        }
        return -1;
    }

    private JsonNode findByTitle(JsonNode items, String title) {
        for (JsonNode item : items) {
            if (title.equals(item.get("guidelineTitle").asText())) {
                return item;
            }
        }
        return null;
    }

    private JsonNode findByEmail(JsonNode items, String email) {
        for (JsonNode item : items) {
            if (email.equals(item.get("email").asText())) {
                return item;
            }
        }
        throw new AssertionError("Ranking sem " + email);
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

    private void approve(String token, String ideaId) throws Exception {
        mockMvc.perform(patch("/api/v1/ideas/" + ideaId + "/status")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"APPROVED\",\"justification\":null}"))
                .andExpect(status().isOk());
    }

    private JsonNode createProject(String token, String ideaId) throws Exception {
        return read(mockMvc.perform(post("/api/v1/projects")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"ideaId\":\"" + ideaId + "\"}"))
                .andExpect(status().isCreated())
                .andReturn());
    }

    private void updateProject(
            String token,
            String projectId,
            String title,
            String projectStatus,
            String investment,
            String profit,
            String productivity,
            String guidelineIdJson
    ) throws Exception {
        mockMvc.perform(put("/api/v1/projects/" + projectId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"%s","description":"Descrição atualizada do projeto com tamanho válido.","status":"%s","investmentAmount":%s,"obtainedProfit":%s,"productivityGainPercent":%s,"deadline":null,"guidelineId":%s}
                                """.formatted(title, projectStatus, investment, profit, productivity, guidelineIdJson)))
                .andExpect(status().isOk());
    }

    private Session register(String name, String email, String role) throws Exception {
        JsonNode body = read(mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"%s","email":"%s","password":"senha123","role":"%s"}
                                """.formatted(name, email, role)))
                .andExpect(status().isCreated())
                .andReturn());
        return new Session(body.get("accessToken").asText(), body.get("user").get("id").asText(), email);
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
            JsonNode body = read(mockMvc.perform(post("/api/v1/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {"email":"%s","password":"%s"}
                                    """.formatted(email, password)))
                    .andExpect(status().isOk())
                    .andReturn());
            Session created = new Session(body.get("accessToken").asText(), body.get("user").get("id").asText(), email);
            SESSIONS.put(email, created);
            return created;
        }
    }

    private JsonNode read(MvcResult result) throws Exception {
        return objectMapper.readTree(result.getResponse().getContentAsString());
    }

    private record Session(String token, String userId, String email) {
    }
}
