package br.com.fiap.aguiabranca.ai;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.fiap.aguiabranca.ai.application.GeminiDashboardPayload;
import br.com.fiap.aguiabranca.ai.domain.InsightText;
import br.com.fiap.aguiabranca.dashboard.application.InsightDataset;
import br.com.fiap.aguiabranca.dashboard.application.InsightDataset.StrategyBrief;
import br.com.fiap.aguiabranca.dashboard.domain.PortfolioSummary;

class GeminiDashboardPayloadTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void payloadHasNoPersonPiiAndTruncatesStrategies() throws Exception {
        List<StrategyBrief> strategies = new ArrayList<>();
        for (int index = 0; index < 9; index++) {
            strategies.add(new StrategyBrief("Estratégia " + index, index, index));
        }
        Map<String, Integer> statuses = new LinkedHashMap<>();
        statuses.put("BACKLOG", 2);
        statuses.put("COMPLETED", 4);
        InsightDataset dataset = new InsightDataset(
                new PortfolioSummary(10, 6, 4, 100000, 130000, 30, 8.4, 45.0),
                statuses,
                7,
                9,
                2,
                strategies
        );

        String json = GeminiDashboardPayload.json(dataset);
        JsonNode node = objectMapper.readTree(json);

        assertThat(json).doesNotContain("@");
        assertThat(json).doesNotContain("email");
        assertThat(json).doesNotContain("Ana Operadora");
        assertThat(json).doesNotContain("ideia secreta");
        assertThat(node.has("email")).isFalse();
        assertThat(node.has("name")).isFalse();
        assertThat(node.get("strategies")).hasSize(8);
        assertThat(node.get("strategies").get(0).get("title").asText()).isEqualTo("Estratégia 0");
        assertThat(node.get("overallRoiPercent").asDouble()).isEqualTo(30.0);
        assertThat(node.get("ideasPending").asInt()).isEqualTo(7);
    }

    @Test
    void blankModelTextIsRejectedAndLongTextIsCut() {
        assertThat(InsightText.normalize(null)).isNull();
        assertThat(InsightText.normalize("   ")).isNull();
        assertThat(InsightText.normalize("ok")).isEqualTo("ok");
        String huge = "a".repeat(8001);
        String normalized = InsightText.normalize(huge);
        assertThat(normalized).hasSize(4003);
        assertThat(normalized).endsWith("...");
        assertThat(InsightText.fallback(60.0, 100.0, 2)).contains("fallback local");
        assertThat(InsightText.DISCLAIMER).contains("não é um fato independente");
    }
}
