package br.com.fiap.aguiabranca.insight;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.fiap.aguiabranca.AbstractMongoIT;
import br.com.fiap.aguiabranca.insight.application.DailyInsightService;
import br.com.fiap.aguiabranca.insight.infra.AdviceSlipAdvice;
import br.com.fiap.aguiabranca.insight.infra.AdviceSlipClient;
import br.com.fiap.aguiabranca.insight.infra.AdviceUnavailableException;

@AutoConfigureMockMvc
class InsightAcceptanceIT extends AbstractMongoIT {

    private static final Map<String, String> TOKENS = new ConcurrentHashMap<>();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DailyInsightService dailyInsightService;

    @MockitoBean
    private AdviceSlipClient adviceSlipClient;

    @BeforeEach
    void resetInsight() {
        dailyInsightService.evict();
        org.mockito.Mockito.reset(adviceSlipClient);
    }

    @Test
    @DisplayName("A-INS-01 insight diário 200 com AdviceSlip e 200 fallback quando indisponível")
    void aIns01_successAndFallbackStay200() throws Exception {
        String token = login("operador@innovatecorp.com", "oper123");
        when(adviceSlipClient.fetch()).thenReturn(new AdviceSlipAdvice(123, "Keep it simple."));

        JsonNode available = getDaily(token);
        assertThat(available.get("id").asInt()).isEqualTo(123);
        assertThat(available.get("message").asText()).isEqualTo("Keep it simple.");

        when(adviceSlipClient.fetch()).thenThrow(new AdviceUnavailableException(true));
        JsonNode cached = getDaily(token);
        assertThat(cached.get("id").asInt()).isEqualTo(123);
        verify(adviceSlipClient, times(1)).fetch();

        dailyInsightService.evict();
        JsonNode fallback = getDaily(token);
        assertThat(fallback.get("id").asInt()).isZero();
        assertThat(fallback.get("message").asText())
                .isEqualTo("Pequenas ideias consistentes constroem grandes resultados.");

        String manager = login("gestor@innovatecorp.com", "gest123");
        JsonNode forManager = getDaily(manager);
        assertThat(forManager.get("id").asInt()).isZero();

        mockMvc.perform(get("/api/v1/insights/daily"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("OpenAPI publica GET /insights/daily")
    void openApi_exposesDailyInsight() throws Exception {
        MvcResult docs = mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode daily = objectMapper.readTree(docs.getResponse().getContentAsString())
                .get("paths")
                .get("/api/v1/insights/daily");
        assertThat(daily.get("get")).isNotNull();
    }

    private JsonNode getDaily(String token) throws Exception {
        MvcResult result = mockMvc.perform(get("/api/v1/insights/daily")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString());
    }

    private String login(String email, String password) throws Exception {
        String cached = TOKENS.get(email);
        if (cached != null) {
            return cached;
        }
        synchronized (TOKENS) {
            cached = TOKENS.get(email);
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
            String token = objectMapper.readTree(result.getResponse().getContentAsString()).get("accessToken").asText();
            TOKENS.put(email, token);
            return token;
        }
    }
}
