package br.com.fiap.aguiabranca.ai.application;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import br.com.fiap.aguiabranca.dashboard.application.InsightDataset;
import br.com.fiap.aguiabranca.dashboard.domain.PortfolioSummary;

public final class GeminiDashboardPayload {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final int MAX_STRATEGIES = 8;

    private GeminiDashboardPayload() {
    }

    public static String json(InsightDataset dataset) {
        PortfolioSummary portfolio = dataset.portfolio();
        ObjectNode root = MAPPER.createObjectNode();
        root.put("totalProjects", portfolio.totalProjects());
        root.put("activeProjects", portfolio.activeProjectsCount());
        root.put("completedProjects", portfolio.completedProjectsCount());
        root.put("totalInvestment", round2(portfolio.totalInvestment()));
        root.put("totalObtainedProfit", round2(portfolio.totalObtainedProfit()));
        root.put("overallRoiPercent", round2(portfolio.overallRoiPercent()));
        root.put("averageProductivityGainPercent", round2(portfolio.averageProductivityGainPercent()));
        if (portfolio.averageDeadlineDays() == null) {
            root.putNull("averageDeadlineDays");
        } else {
            root.put("averageDeadlineDays", round2(portfolio.averageDeadlineDays()));
        }
        ObjectNode statuses = root.putObject("projectsByStatus");
        for (Map.Entry<String, Integer> entry : dataset.projectsByStatus().entrySet()) {
            statuses.put(entry.getKey(), entry.getValue());
        }
        root.put("ideasPending", dataset.ideasPending());
        root.put("ideasApprovedOrPrioritized", dataset.ideasApprovedOrPrioritized());
        root.put("ideasRejected", dataset.ideasRejected());
        ArrayNode strategies = root.putArray("strategies");
        dataset.strategies().stream().limit(MAX_STRATEGIES).forEach(strategy -> {
            ObjectNode item = strategies.addObject();
            item.put("title", strategy.title());
            item.put("projects", strategy.projects());
            item.put("roiPercent", round2(strategy.roiPercent()));
        });
        try {
            return MAPPER.writeValueAsString(root);
        } catch (com.fasterxml.jackson.core.JsonProcessingException ex) {
            throw new IllegalStateException("Falha ao montar o payload do dashboard", ex);
        }
    }

    private static double round2(double value) {
        if (!Double.isFinite(value)) {
            return 0.0;
        }
        return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}
