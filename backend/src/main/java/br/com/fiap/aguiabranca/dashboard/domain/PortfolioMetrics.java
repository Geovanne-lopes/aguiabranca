package br.com.fiap.aguiabranca.dashboard.domain;

import java.time.Instant;
import java.util.List;

import br.com.fiap.aguiabranca.project.domain.ProjectRoi;
import br.com.fiap.aguiabranca.project.domain.ProjectStatus;

public final class PortfolioMetrics {

    static final double MILLIS_PER_DAY = 86_400_000.0;

    private PortfolioMetrics() {
    }

    public static PortfolioSummary summarize(List<ProjectFigures> projects) {
        if (projects == null || projects.isEmpty()) {
            return new PortfolioSummary(0, 0, 0, 0.0, 0.0, 0.0, 0.0, null);
        }
        double investment = 0.0;
        double profit = 0.0;
        double productivity = 0.0;
        int active = 0;
        int completed = 0;
        double deadlineDays = 0.0;
        int withDeadline = 0;
        for (ProjectFigures project : projects) {
            investment += project.investmentAmount();
            profit += project.obtainedProfit();
            productivity += project.productivityGainPercent();
            if (project.status() == ProjectStatus.COMPLETED) {
                completed++;
            } else {
                active++;
            }
            Double days = deadlineDays(project.createdAt(), project.deadline());
            if (days != null) {
                deadlineDays += days;
                withDeadline++;
            }
        }
        int total = projects.size();
        Double averageDeadline = withDeadline == 0 ? null : deadlineDays / withDeadline;
        return new PortfolioSummary(
                total,
                active,
                completed,
                finite(investment),
                finite(profit),
                ProjectRoi.percent(investment, profit),
                finite(productivity / total),
                averageDeadline == null ? null : finite(averageDeadline)
        );
    }

    static Double deadlineDays(Instant createdAt, Instant deadline) {
        if (deadline == null || createdAt == null) {
            return null;
        }
        double days = (deadline.toEpochMilli() - createdAt.toEpochMilli()) / MILLIS_PER_DAY;
        return Math.max(0.0, days);
    }

    private static double finite(double value) {
        if (!Double.isFinite(value)) {
            return 0.0;
        }
        return value;
    }
}
