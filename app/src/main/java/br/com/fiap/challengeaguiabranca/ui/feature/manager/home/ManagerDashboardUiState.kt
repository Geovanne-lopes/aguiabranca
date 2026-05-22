package br.com.fiap.challengeaguiabranca.ui.feature.manager.home

data class MonthBarData(
    val label: String,
    val count: Int
)

data class ManagerDashboardUiState(
    val userFirstName: String = "",
    val pendingIdeasCount: Int = 0,
    val activeProjectsCount: Int = 0,
    val ideasReceivedCount: Int = 0,
    val approvalRatePercent: Int = 0,
    val ideasReceivedTrend: String = "",
    val approvalRateTrend: String = "",
    val monthlyBars: List<MonthBarData> = emptyList(),
    val isLoading: Boolean = true
)
