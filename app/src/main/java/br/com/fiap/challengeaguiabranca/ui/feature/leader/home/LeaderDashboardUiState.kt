package br.com.fiap.challengeaguiabranca.ui.feature.leader.home

import br.com.fiap.challengeaguiabranca.domain.model.RoiDashboardSummary
import br.com.fiap.challengeaguiabranca.domain.model.StrategyReturn

data class LeaderDashboardUiState(
    val userFirstName: String = "",
    val summary: RoiDashboardSummary = RoiDashboardSummary(
        totalInvestment = 0.0,
        totalObtainedProfit = 0.0,
        overallRoiPercent = 0.0,
        averageProductivityGainPercent = 0.0,
        activeProjectsCount = 0,
        completedProjectsCount = 0
    ),
    val statusChartLabels: List<String> = emptyList(),
    val statusChartValues: List<Int> = emptyList(),
    val strategyReturns: List<StrategyReturn> = emptyList(),
    val isLoading: Boolean = true,
    val aiInsightText: String? = null,
    val aiInsightDisclaimer: String? = null,
    val aiInsightLoading: Boolean = false,
    val aiInsightError: String? = null
)
