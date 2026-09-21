package br.com.fiap.challengeaguiabranca.domain.model

data class MonthCount(
    val label: String,
    val count: Int
)

data class ManagerDashboard(
    val pendingIdeasCount: Int,
    val activeProjectsCount: Int,
    val ideasReceivedThisMonth: Int,
    val ideasReceivedTrendLabel: String,
    val approvalRatePercent: Int,
    val approvalRateTrendLabel: String,
    val monthlyBars: List<MonthCount>
)

data class StrategyReturn(
    val guidelineId: String?,
    val guidelineTitle: String,
    val ideasCount: Int,
    val projectsCount: Int,
    val totalInvestment: Double,
    val totalObtainedProfit: Double,
    val overallRoiPercent: Double,
    val averageProductivityGainPercent: Double,
    val completedProjectsCount: Int
)

data class LeaderDashboard(
    val summary: RoiDashboardSummary,
    val statusChartLabels: List<String>,
    val statusChartValues: List<Int>
)
