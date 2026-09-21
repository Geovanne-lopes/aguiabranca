package br.com.fiap.aguiabranca.dashboard.application;

import java.util.List;
import java.util.Map;

import br.com.fiap.aguiabranca.dashboard.domain.PortfolioSummary;

/**
 * Totais já calculados para o Gemini. Não carrega e-mail, nome de pessoa nem texto de ideia.
 */
public record InsightDataset(
        PortfolioSummary portfolio,
        Map<String, Integer> projectsByStatus,
        int ideasPending,
        int ideasApprovedOrPrioritized,
        int ideasRejected,
        List<StrategyBrief> strategies
) {

    public record StrategyBrief(String title, int projects, double roiPercent) {
    }
}
