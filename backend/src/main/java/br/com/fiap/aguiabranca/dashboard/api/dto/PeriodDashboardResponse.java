package br.com.fiap.aguiabranca.dashboard.api.dto;

import java.time.Instant;
import java.util.List;

public record PeriodDashboardResponse(
        int totalProjects,
        int activeProjectsCount,
        int completedProjectsCount,
        double totalInvestment,
        double totalObtainedProfit,
        double overallRoiPercent,
        double averageProductivityGainPercent,
        Double averageDeadlineDays,
        List<StatusCountResponse> projectsByStatus,
        boolean hasData,
        Instant from,
        Instant to
) {
    public static PeriodDashboardResponse of(LeaderDashboardResponse leader, Instant from, Instant to) {
        return new PeriodDashboardResponse(
                leader.totalProjects(),
                leader.activeProjectsCount(),
                leader.completedProjectsCount(),
                leader.totalInvestment(),
                leader.totalObtainedProfit(),
                leader.overallRoiPercent(),
                leader.averageProductivityGainPercent(),
                leader.averageDeadlineDays(),
                leader.projectsByStatus(),
                leader.hasData(),
                from,
                to
        );
    }
}
