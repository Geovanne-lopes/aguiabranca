package br.com.fiap.aguiabranca.dashboard;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.Test;

import br.com.fiap.aguiabranca.dashboard.domain.PortfolioMetrics;
import br.com.fiap.aguiabranca.dashboard.domain.PortfolioSummary;
import br.com.fiap.aguiabranca.dashboard.domain.ProjectFigures;
import br.com.fiap.aguiabranca.project.domain.ProjectRoi;
import br.com.fiap.aguiabranca.project.domain.ProjectStatus;

class PortfolioMetricsTest {

    @Test
    void emptyPortfolioIsZeroAndHasNoDeadline() {
        PortfolioSummary summary = PortfolioMetrics.summarize(List.of());

        assertThat(summary.totalProjects()).isZero();
        assertThat(summary.totalInvestment()).isEqualTo(0.0);
        assertThat(summary.totalObtainedProfit()).isEqualTo(0.0);
        assertThat(summary.overallRoiPercent()).isEqualTo(0.0);
        assertThat(summary.averageProductivityGainPercent()).isEqualTo(0.0);
        assertThat(summary.activeProjectsCount()).isZero();
        assertThat(summary.completedProjectsCount()).isZero();
        assertThat(summary.averageDeadlineDays()).isNull();
        assertThat(summary.hasData()).isFalse();
    }

    @Test
    void consolidatedRoiUsesProjectRoiOnTotalsIncludingZeroInvestment() {
        PortfolioSummary summary = PortfolioMetrics.summarize(List.of(
                figures(100, 150, 0, ProjectStatus.BACKLOG, null, null),
                figures(0, 10, 0, ProjectStatus.COMPLETED, null, null)
        ));

        assertThat(summary.totalInvestment()).isEqualTo(100.0);
        assertThat(summary.totalObtainedProfit()).isEqualTo(160.0);
        assertThat(summary.overallRoiPercent()).isEqualTo(ProjectRoi.percent(100, 160));
        assertThat(summary.overallRoiPercent()).isCloseTo(60.0, within(0.000001));
        assertThat(summary.activeProjectsCount()).isEqualTo(1);
        assertThat(summary.completedProjectsCount()).isEqualTo(1);
        assertThat(summary.hasData()).isTrue();
    }

    @Test
    void zeroInvestmentAloneStaysZeroRoi() {
        PortfolioSummary summary = PortfolioMetrics.summarize(List.of(
                figures(0, 10, 0, ProjectStatus.BACKLOG, null, null)
        ));

        assertThat(summary.overallRoiPercent()).isEqualTo(0.0);
        assertThat(summary.totalObtainedProfit()).isEqualTo(10.0);
    }

    @Test
    void averageProductivityIncludesZeros() {
        PortfolioSummary summary = PortfolioMetrics.summarize(List.of(
                figures(10, 10, 0, ProjectStatus.BACKLOG, null, null),
                figures(10, 10, 10, ProjectStatus.BACKLOG, null, null)
        ));

        assertThat(summary.averageProductivityGainPercent()).isCloseTo(5.0, within(0.000001));
    }

    @Test
    void averageDeadlineIgnoresMissingAndFloorsNegativeSpans() {
        Instant created = Instant.EPOCH;
        PortfolioSummary summary = PortfolioMetrics.summarize(List.of(
                figures(1, 1, 0, ProjectStatus.BACKLOG, created, created.plusMillis(2 * 86_400_000L)),
                figures(1, 1, 0, ProjectStatus.BACKLOG, created, created.minusMillis(86_400_000L)),
                figures(1, 1, 0, ProjectStatus.BACKLOG, created, null)
        ));

        assertThat(summary.averageDeadlineDays()).isCloseTo(1.0, within(0.000001));
    }

    private static ProjectFigures figures(
            double investment,
            double profit,
            double productivity,
            ProjectStatus status,
            Instant createdAt,
            Instant deadline
    ) {
        return new ProjectFigures(investment, profit, productivity, status, createdAt, deadline, null);
    }
}
