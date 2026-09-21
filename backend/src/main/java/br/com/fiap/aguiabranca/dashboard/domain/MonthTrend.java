package br.com.fiap.aguiabranca.dashboard.domain;

/**
 * Rótulos de tendência iguais a OperatorKpiTrend / ManagerDashboardStats.
 * O percentual inteiro usa o mesmo arredondamento de {@code kotlin.math.roundToInt} (half toward +∞).
 */
public final class MonthTrend {

    private MonthTrend() {
    }

    public static int percent(int current, int previous) {
        if (previous == 0) {
            return 0;
        }
        return (int) Math.round(((current - previous) / (double) previous) * 100.0);
    }

    public static String label(int current, int previous) {
        if (current == 0 && previous == 0) {
            return "Sem dados no período";
        }
        if (previous == 0 && current > 0) {
            return "Primeiro registro neste mês";
        }
        if (current == 0 && previous > 0) {
            return "↓ 100% vs mês anterior";
        }
        int percent = percent(current, previous);
        if (percent > 0) {
            return "↑ " + percent + "% vs mês anterior";
        }
        if (percent < 0) {
            return "↓ " + (-percent) + "% vs mês anterior";
        }
        return "Igual ao mês anterior";
    }

    public static int approvalRatePercent(int approvedOrPrioritized, int totalIdeas) {
        if (totalIdeas <= 0) {
            return 0;
        }
        return (int) Math.round((approvedOrPrioritized / (double) totalIdeas) * 100.0);
    }

    public static String approvalRateLabel(int totalIdeas, int approvalRatePercent) {
        if (totalIdeas <= 0) {
            return "Sem ideias para calcular";
        }
        return approvalRatePercent + "% no total";
    }
}
