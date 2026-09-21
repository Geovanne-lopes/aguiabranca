package br.com.fiap.challengeaguiabranca.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class StatusCountDto(
    val status: String = "",
    val label: String = "",
    val count: Int = 0
)

@Serializable
data class MonthBarDto(
    val label: String = "",
    val count: Int = 0
)

@Serializable
data class ManagerDashboardDto(
    val pendingIdeasCount: Int = 0,
    val activeProjectsCount: Int = 0,
    val ideasReceivedThisMonth: Int = 0,
    val ideasReceivedPreviousMonth: Int = 0,
    val ideasReceivedTrendLabel: String = "",
    val approvalRatePercent: Int = 0,
    val approvalRateTrendLabel: String = "",
    val monthlyBars: List<MonthBarDto> = emptyList(),
    val ideasByStatus: List<StatusCountDto> = emptyList(),
    val hasData: Boolean = false
)

@Serializable
data class LeaderDashboardDto(
    val totalProjects: Int = 0,
    val activeProjectsCount: Int = 0,
    val completedProjectsCount: Int = 0,
    val totalInvestment: Double = 0.0,
    val totalObtainedProfit: Double = 0.0,
    val overallRoiPercent: Double = 0.0,
    val averageProductivityGainPercent: Double = 0.0,
    val projectsByStatus: List<StatusCountDto> = emptyList(),
    val hasData: Boolean = false
)

@Serializable
data class RankingItemDto(
    val authorId: String,
    val name: String = "",
    val email: String = "",
    val ideasSubmitted: Int = 0,
    val ideasApproved: Int = 0
)

@Serializable
data class StrategyReturnDto(
    val guidelineId: String? = null,
    val guidelineTitle: String = "",
    val ideasCount: Int = 0,
    val projectsCount: Int = 0,
    val totalInvestment: Double = 0.0,
    val totalObtainedProfit: Double = 0.0,
    val overallRoiPercent: Double = 0.0,
    val averageProductivityGainPercent: Double = 0.0,
    val completedProjectsCount: Int = 0
)

@Serializable
data class StrategiesResponseDto(
    val items: List<StrategyReturnDto> = emptyList()
)

@Serializable
data class RankingsResponseDto(
    val items: List<RankingItemDto> = emptyList()
)

@Serializable
data class NotificationItemDto(
    val id: String,
    val title: String = "",
    val body: String = "",
    val type: String = "",
    val createdAt: String? = null
)

@Serializable
data class NotificationsResponseDto(
    val items: List<NotificationItemDto> = emptyList(),
    val unreadHint: Boolean = false
)

@Serializable
data class AiInsightResponseDto(
    val id: String,
    val content: String = "",
    val source: String = "",
    val disclaimer: String = "",
    val createdAt: String? = null
)
