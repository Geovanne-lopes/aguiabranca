package br.com.fiap.aguiabranca.dashboard.api.dto;

public record StrategyItemResponse(
        String guidelineId,
        String guidelineTitle,
        int ideasCount,
        int projectsCount,
        double totalInvestment,
        double totalObtainedProfit,
        double overallRoiPercent,
        double averageProductivityGainPercent,
        int completedProjectsCount
) {
}
