package br.com.fiap.aguiabranca.dashboard.api.dto;

import java.util.List;

public record LeaderDashboardResponse(
        int totalProjects,
        int activeProjectsCount,
        int completedProjectsCount,
        double totalInvestment,
        double totalObtainedProfit,
        double overallRoiPercent,
        double averageProductivityGainPercent,
        Double averageDeadlineDays,
        List<StatusCountResponse> projectsByStatus,
        boolean hasData
) {
}
