package br.com.fiap.aguiabranca.dashboard.domain;

/**
 * Agregado de portfólio. O ROI consolidado usa {@link br.com.fiap.aguiabranca.project.domain.ProjectRoi}
 * sobre os totais, a mesma fórmula do ROI de um projeto.
 */
public record PortfolioSummary(
        int totalProjects,
        int activeProjectsCount,
        int completedProjectsCount,
        double totalInvestment,
        double totalObtainedProfit,
        double overallRoiPercent,
        double averageProductivityGainPercent,
        Double averageDeadlineDays
) {

    public boolean hasData() {
        return totalProjects > 0;
    }
}
