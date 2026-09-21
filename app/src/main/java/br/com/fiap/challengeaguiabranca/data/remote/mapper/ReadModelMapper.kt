package br.com.fiap.challengeaguiabranca.data.remote.mapper

import br.com.fiap.challengeaguiabranca.data.remote.dto.AiInsightResponseDto
import br.com.fiap.challengeaguiabranca.data.remote.dto.DailyInsightResponseDto
import br.com.fiap.challengeaguiabranca.data.remote.dto.LeaderDashboardDto
import br.com.fiap.challengeaguiabranca.data.remote.dto.ManagerDashboardDto
import br.com.fiap.challengeaguiabranca.data.remote.dto.NotificationItemDto
import br.com.fiap.challengeaguiabranca.data.remote.dto.RankingItemDto
import br.com.fiap.challengeaguiabranca.data.remote.dto.StrategyReturnDto
import br.com.fiap.challengeaguiabranca.data.remote.dto.SuggestionResponseDto
import br.com.fiap.challengeaguiabranca.domain.model.AiInsight
import br.com.fiap.challengeaguiabranca.domain.model.DailyInsight
import br.com.fiap.challengeaguiabranca.domain.model.LeaderDashboard
import br.com.fiap.challengeaguiabranca.domain.model.ManagerDashboard
import br.com.fiap.challengeaguiabranca.domain.model.ManagerNotification
import br.com.fiap.challengeaguiabranca.domain.model.ManagerNotificationType
import br.com.fiap.challengeaguiabranca.domain.model.ManagerSuggestion
import br.com.fiap.challengeaguiabranca.domain.model.MonthCount
import br.com.fiap.challengeaguiabranca.domain.model.OperatorActivity
import br.com.fiap.challengeaguiabranca.domain.model.OperatorNotification
import br.com.fiap.challengeaguiabranca.domain.model.OperatorNotificationType
import br.com.fiap.challengeaguiabranca.domain.model.RoiDashboardSummary
import br.com.fiap.challengeaguiabranca.domain.model.StrategyReturn

fun SuggestionResponseDto.toDomain(): ManagerSuggestion = ManagerSuggestion(
    id = id,
    managerName = authorName,
    targetAuthorId = targetUserId,
    targetEmail = targetEmail,
    targetName = targetName,
    message = message,
    createdAtEpochMillis = createdAt.toEpochMillis()
)

fun DailyInsightResponseDto.toDomain(): DailyInsight = DailyInsight(
    id = id,
    message = message
)

fun ManagerDashboardDto.toDomain(): ManagerDashboard = ManagerDashboard(
    pendingIdeasCount = pendingIdeasCount,
    activeProjectsCount = activeProjectsCount,
    ideasReceivedThisMonth = ideasReceivedThisMonth,
    ideasReceivedTrendLabel = ideasReceivedTrendLabel,
    approvalRatePercent = approvalRatePercent,
    approvalRateTrendLabel = approvalRateTrendLabel,
    monthlyBars = monthlyBars.map { MonthCount(label = it.label, count = it.count) }
)

fun LeaderDashboardDto.toDomain(): LeaderDashboard = LeaderDashboard(
    summary = RoiDashboardSummary(
        totalInvestment = totalInvestment,
        totalObtainedProfit = totalObtainedProfit,
        overallRoiPercent = overallRoiPercent,
        averageProductivityGainPercent = averageProductivityGainPercent,
        activeProjectsCount = activeProjectsCount,
        completedProjectsCount = completedProjectsCount
    ),
    statusChartLabels = projectsByStatus.map { it.label.ifBlank { it.status } },
    statusChartValues = projectsByStatus.map { it.count }
)

fun StrategyReturnDto.toDomain(): StrategyReturn = StrategyReturn(
    guidelineId = guidelineId,
    guidelineTitle = guidelineTitle,
    ideasCount = ideasCount,
    projectsCount = projectsCount,
    totalInvestment = totalInvestment,
    totalObtainedProfit = totalObtainedProfit,
    overallRoiPercent = overallRoiPercent,
    averageProductivityGainPercent = averageProductivityGainPercent,
    completedProjectsCount = completedProjectsCount
)

fun RankingItemDto.toDomain(): OperatorActivity = OperatorActivity(
    authorId = authorId,
    name = name,
    email = email,
    ideasSubmitted = ideasSubmitted,
    ideasApproved = ideasApproved
)

fun AiInsightResponseDto.toDomain(): AiInsight = AiInsight(
    id = id,
    content = content,
    source = source,
    disclaimer = disclaimer
)

fun NotificationItemDto.toOperatorNotification(): OperatorNotification? {
    val mapped = when (type) {
        "GUIDELINE", "SUGGESTION" -> OperatorNotificationType.GUIDELINE
        "IDEA_PENDING" -> OperatorNotificationType.IDEA_PENDING
        "IDEA_APPROVED" -> OperatorNotificationType.IDEA_APPROVED
        "IDEA_REJECTED" -> OperatorNotificationType.IDEA_REJECTED
        "IDEA_PRIORITIZED" -> OperatorNotificationType.IDEA_PRIORITIZED
        else -> return null
    }
    return OperatorNotification(
        id = id,
        title = title,
        body = body,
        type = mapped,
        timestampEpochMillis = createdAt.toEpochMillis()
    )
}

fun NotificationItemDto.toManagerNotification(): ManagerNotification {
    val mapped = when (type) {
        "PENDING_IDEAS" -> ManagerNotificationType.PENDING_IDEAS
        "NEW_IDEA", "IDEA_PENDING" -> ManagerNotificationType.NEW_IDEA
        "SUGGESTION_SENT", "SUGGESTION", "GUIDELINE" -> ManagerNotificationType.SUGGESTION_SENT
        else -> ManagerNotificationType.TEAM_ACTIVITY
    }
    return ManagerNotification(
        id = id,
        title = title,
        body = body,
        type = mapped,
        timestampEpochMillis = createdAt.toEpochMillis()
    )
}
