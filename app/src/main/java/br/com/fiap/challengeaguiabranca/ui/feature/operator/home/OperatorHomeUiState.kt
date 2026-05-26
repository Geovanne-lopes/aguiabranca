package br.com.fiap.challengeaguiabranca.ui.feature.operator.home

import br.com.fiap.challengeaguiabranca.domain.model.DailyInsight
import br.com.fiap.challengeaguiabranca.domain.model.Idea
import br.com.fiap.challengeaguiabranca.domain.model.OperatorNotification
import br.com.fiap.challengeaguiabranca.domain.model.StrategicGuideline
import br.com.fiap.challengeaguiabranca.ui.util.GamificationState

data class OperatorHomeUiState(
    val userFirstName: String = "",
    val userFullName: String = "",
    val userEmail: String = "",
    val gamification: GamificationState = GamificationState(1, 250, 0f, 2),
    val gamificationLevel: Int = 1,
    val gamificationPoints: Int = 0,
    val ideasSubmittedCount: Int = 0,
    val ideasApprovedCount: Int = 0,
    val submittedTrendLabel: String = "",
    val approvedTrendLabel: String = "",
    val insight: DailyInsight? = null,
    val insightLoading: Boolean = false,
    val insightError: String? = null,
    val allGuidelines: List<StrategicGuideline> = emptyList(),
    val featuredGuideline: StrategicGuideline? = null,
    val allIdeas: List<Idea> = emptyList(),
    val recentIdeas: List<Idea> = emptyList(),
    val notifications: List<OperatorNotification> = emptyList(),
    val hasUnreadNotifications: Boolean = false,
    val isLoading: Boolean = true
)

enum class OperatorTab {
    HOME,
    STRATEGIES,
    IDEAS,
    PROFILE
}
