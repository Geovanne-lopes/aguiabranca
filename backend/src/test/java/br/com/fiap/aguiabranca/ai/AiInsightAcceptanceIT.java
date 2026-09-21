package br.com.fiap.aguiabranca.ai;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bson.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.fiap.aguiabranca.AbstractMongoIT;
import br.com.fiap.aguiabranca.ai.domain.InsightText;
import br.com.fiap.aguiabranca.ai.infra.GeminiClient;
import br.com.fiap.aguiabranca.ai.infra.GeminiCompletion;
import br.com.fiap.aguiabranca.ai.infra.GeminiProperties;

@AutoConfigureMockMvc
class AiInsightAcceptanceIT extends AbstractMongoIT {

    private static final Map<String, Session> SESSIONS = new ConcurrentHashMap<>();

    @DynamicPropertySource
    static void geminiKey(DynamicPropertyRegistry registry) {
        registry.add("app.gemini.api-key", () -> "test-gemini-key-not-a-real-secret");
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private GeminiProperties geminiProperties;

    @MockitoBean
    private GeminiClient geminiClient;

    private String configuredKey;

    @BeforeEach
    void resetGemini() {
        if (configuredKey == null) {
            configuredKey = geminiProperties.getApiKey();
        }
        geminiProperties.setApiKey(configuredKey);
        org.mockito.Mockito.reset(geminiClient);
    }

    @Test
    @DisplayName("A-AI-01 líder recebe GEMINI e, no timeout, FALLBACK 200; gestor e operador 403")
    void aAi01_successTimeoutAndForbiddenRoles() throws Exception {
        Session leader = login("lideranca@innovatecorp.com", "lider123");
        Session manager = login("gestor@innovatecorp.com", "gest123");
        Session operator = login("operador@innovatecorp.com", "oper123");

        mockMvc.perform(post("/api/v1/ai/insights")
                        .header("Authorization", "Bearer " + manager.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("FORBIDDEN"));
        mockMvc.perform(post("/api/v1/ai/insights")
                        .header("Authorization", "Bearer " + operator.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isForbidden());
        verify(geminiClient, never()).complete(anyString());

        when(geminiClient.complete(anyString())).thenReturn(
                GeminiCompletion.success("O ROI consolidado indica concentração em execução.", "gemini-2.0-flash")
        );
        JsonNode generated = read(mockMvc.perform(post("/api/v1/ai/insights")
                        .header("Authorization", "Bearer " + leader.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk())
                .andReturn());

        assertThat(generated.get("source").asText()).isEqualTo("GEMINI");
        assertThat(generated.get("content").asText()).isNotBlank();
        assertThat(generated.get("disclaimer").asText()).isEqualTo(InsightText.DISCLAIMER);
        assertThat(generated.get("basedOn").has("email")).isFalse();
        assertThat(generated.get("basedOn").toString()).doesNotContain("@");
        assertThat(generated.get("basedOn").has("totalProjects")).isTrue();
        assertThat(generated.get("basedOn").has("overallRoiPercent")).isTrue();
        assertThat(generated.get("basedOn").has("totalInvestment")).isTrue();

        ArgumentCaptor<String> payload = ArgumentCaptor.forClass(String.class);
        verify(geminiClient).complete(payload.capture());
        assertThat(payload.getValue()).doesNotContain("@").doesNotContain("email").doesNotContain("Ana Operadora");

        Document stored = mongoTemplate.findById(generated.get("id").asText(), Document.class, "ai_insights");
        assertThat(stored).isNotNull();
        assertThat(stored.getString("status")).isEqualTo("SUCCESS");
        assertThat(stored.getInteger("promptVersion")).isEqualTo(1);
        assertThat(stored.get("inputSummary").toString()).doesNotContain("@");

        when(geminiClient.complete(anyString())).thenReturn(GeminiCompletion.timeout("gemini-2.0-flash"));
        JsonNode fallback = read(mockMvc.perform(post("/api/v1/ai/insights")
                        .header("Authorization", "Bearer " + leader.token()))
                .andExpect(status().isOk())
                .andReturn());
        assertThat(fallback.get("source").asText()).isEqualTo("FALLBACK");
        assertThat(fallback.get("content").asText()).contains("fallback local");
        assertThat(fallback.get("disclaimer").asText()).isEqualTo(InsightText.DISCLAIMER);
    }

    @Test
    @DisplayName("Resposta vazia do Gemini vira FALLBACK 200")
    void emptyGeminiResponseFallsBack() throws Exception {
        Session leader = login("lideranca@innovatecorp.com", "lider123");
        when(geminiClient.complete(anyString())).thenReturn(GeminiCompletion.empty("gemini-2.0-flash"));

        JsonNode body = read(mockMvc.perform(post("/api/v1/ai/insights")
                        .header("Authorization", "Bearer " + leader.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk())
                .andReturn());

        assertThat(body.get("source").asText()).isEqualTo("FALLBACK");
        assertThat(body.get("content").asText()).contains("fallback local");
    }

    @Test
    @DisplayName("Key ausente não chama a rede e devolve FALLBACK 200")
    void missingKeyDoesNotCallGemini() throws Exception {
        Session leader = login("lideranca@innovatecorp.com", "lider123");
        geminiProperties.setApiKey("  ");

        JsonNode body = read(mockMvc.perform(post("/api/v1/ai/insights")
                        .header("Authorization", "Bearer " + leader.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk())
                .andReturn());

        assertThat(body.get("source").asText()).isEqualTo("FALLBACK");
        verify(geminiClient, never()).complete(anyString());
        Document stored = mongoTemplate.findOne(
                Query.query(Criteria.where("_id").is(body.get("id").asText())),
                Document.class,
                "ai_insights"
        );
        assertThat(stored.getString("status")).isEqualTo("FALLBACK");
    }

    @Test
    @DisplayName("GET /ai/insights/latest é 404 até o líder gerar e 403 para o gestor")
    void latestIsNotFoundUntilGenerated() throws Exception {
        String suffix = UUID.randomUUID().toString().substring(0, 8);
        Session leader = register("Líder " + suffix, "lider." + suffix + "@innovatecorp.com");
        Session manager = login("gestor@innovatecorp.com", "gest123");

        mockMvc.perform(get("/api/v1/ai/insights/latest").header("Authorization", "Bearer " + manager.token()))
                .andExpect(status().isForbidden());
        mockMvc.perform(get("/api/v1/ai/insights/latest").header("Authorization", "Bearer " + leader.token()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("NOT_FOUND"));

        when(geminiClient.complete(anyString())).thenReturn(
                GeminiCompletion.success("Leitura do consolidado para a liderança.", "gemini-2.0-flash")
        );
        JsonNode created = read(mockMvc.perform(post("/api/v1/ai/insights")
                        .header("Authorization", "Bearer " + leader.token()))
                .andExpect(status().isOk())
                .andReturn());
        JsonNode latest = read(mockMvc.perform(get("/api/v1/ai/insights/latest")
                        .header("Authorization", "Bearer " + leader.token()))
                .andExpect(status().isOk())
                .andReturn());
        assertThat(latest.get("id").asText()).isEqualTo(created.get("id").asText());
        assertThat(latest.get("source").asText()).isEqualTo("GEMINI");
    }

    @Test
    @DisplayName("OpenAPI publica POST /ai/insights e GET /ai/insights/latest")
    void openApi_exposesAiInsights() throws Exception {
        JsonNode paths = objectMapper.readTree(mockMvc.perform(get("/v3/api-docs"))
                        .andExpect(status().isOk())
                        .andReturn()
                        .getResponse()
                        .getContentAsString())
                .get("paths");
        assertThat(paths.get("/api/v1/ai/insights").get("post")).isNotNull();
        assertThat(paths.get("/api/v1/ai/insights/latest").get("get")).isNotNull();
    }

    private Session register(String name, String email) throws Exception {
        JsonNode body = read(mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"%s","email":"%s","password":"senha123","role":"LEADER"}
                                """.formatted(name, email)))
                .andExpect(status().isCreated())
                .andReturn());
        return new Session(body.get("accessToken").asText(), email);
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
            Session created = new Session(body.get("accessToken").asText(), email);
            SESSIONS.put(email, created);
            return created;
        }
    }

    private JsonNode read(MvcResult result) throws Exception {
        return objectMapper.readTree(result.getResponse().getContentAsString());
    }

    private record Session(String token, String email) {
    }
}
