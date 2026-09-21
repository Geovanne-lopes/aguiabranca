package br.com.fiap.aguiabranca.dashboard;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

import org.junit.jupiter.api.Test;

import br.com.fiap.aguiabranca.dashboard.domain.MonthBar;
import br.com.fiap.aguiabranca.dashboard.domain.MonthTrend;
import br.com.fiap.aguiabranca.dashboard.domain.OperatorKpis;
import br.com.fiap.aguiabranca.dashboard.domain.OperatorKpis.IdeaPoint;
import br.com.fiap.aguiabranca.dashboard.domain.RankedAuthor;
import br.com.fiap.aguiabranca.dashboard.domain.RankingSorter;
import br.com.fiap.aguiabranca.dashboard.domain.SaoPauloCalendar;
import br.com.fiap.aguiabranca.idea.domain.IdeaStatus;

class KpiTrendsTest {

    private static final Clock JUNE_2026 = Clock.fixed(Instant.parse("2026-06-15T15:00:00Z"), ZoneOffset.UTC);

    @Test
    void trendLabelsMatchTheSpecTable() {
        assertThat(MonthTrend.label(0, 0)).isEqualTo("Sem dados no período");
        assertThat(MonthTrend.label(4, 0)).isEqualTo("Primeiro registro neste mês");
        assertThat(MonthTrend.percent(4, 0)).isZero();
        assertThat(MonthTrend.label(0, 5)).isEqualTo("↓ 100% vs mês anterior");
        assertThat(MonthTrend.percent(0, 5)).isEqualTo(-100);
        assertThat(MonthTrend.label(5, 4)).isEqualTo("↑ 25% vs mês anterior");
        assertThat(MonthTrend.label(1, 2)).isEqualTo("↓ 50% vs mês anterior");
        assertThat(MonthTrend.label(4, 4)).isEqualTo("Igual ao mês anterior");
    }

    @Test
    void approvalRateRoundsHalfAwayFromZeroLikeKotlin() {
        assertThat(MonthTrend.approvalRatePercent(0, 0)).isZero();
        assertThat(MonthTrend.approvalRatePercent(1, 2)).isEqualTo(50);
        assertThat(MonthTrend.approvalRatePercent(1, 8)).isEqualTo(13);
        assertThat(MonthTrend.approvalRateLabel(0, 0)).isEqualTo("Sem ideias para calcular");
        assertThat(MonthTrend.approvalRateLabel(10, 40)).isEqualTo("40% no total");
    }

    @Test
    void monthlyBarsUseSaoPauloAndFiveMonths() {
        List<MonthBar> bars = SaoPauloCalendar.lastMonths(List.of(
                Instant.parse("2026-06-02T15:00:00Z"),
                Instant.parse("2026-05-10T15:00:00Z")
        ), JUNE_2026, 5);

        assertThat(bars).extracting(MonthBar::label).containsExactly("Fev", "Mar", "Abr", "Mai", "Jun");
        assertThat(bars).extracting(MonthBar::count).containsExactly(0, 0, 0, 1, 1);
    }

    @Test
    void approvedTrendUsesFirstDecisionInstantNotCreatedAt() {
        OperatorKpis.Result result = OperatorKpis.compute(List.of(
                new IdeaPoint(
                        IdeaStatus.APPROVED,
                        Instant.parse("2026-05-10T15:00:00Z"),
                        Instant.parse("2026-06-02T15:00:00Z")
                ),
                new IdeaPoint(
                        IdeaStatus.PRIORITIZED,
                        Instant.parse("2026-06-03T15:00:00Z"),
                        Instant.parse("2026-06-04T15:00:00Z")
                )
        ), JUNE_2026);

        assertThat(result.ideasSubmittedCount()).isEqualTo(2);
        assertThat(result.ideasApprovedCount()).isEqualTo(1);
        assertThat(result.ideasPrioritizedCount()).isEqualTo(1);
        assertThat(result.submittedThisMonth()).isEqualTo(1);
        assertThat(result.submittedPreviousMonth()).isEqualTo(1);
        assertThat(result.submittedTrendLabel()).isEqualTo("Igual ao mês anterior");
        assertThat(result.approvedThisMonth()).isEqualTo(2);
        assertThat(result.approvedPreviousMonth()).isZero();
        assertThat(result.approvedTrendLabel()).isEqualTo("Primeiro registro neste mês");
        assertThat(result.hasData()).isTrue();
    }

    @Test
    void rankingSortsSubmittedDescThenNameAsc() {
        List<RankedAuthor> sorted = RankingSorter.sort(List.of(
                new RankedAuthor("2", "Bruno", "bruno@innovatecorp.com", 3, 1),
                new RankedAuthor("3", "Zeca", "zeca@innovatecorp.com", 1, 0),
                new RankedAuthor("1", "Ana", "ana@innovatecorp.com", 3, 2)
        ));

        assertThat(sorted).extracting(RankedAuthor::name).containsExactly("Ana", "Bruno", "Zeca");
    }
}
