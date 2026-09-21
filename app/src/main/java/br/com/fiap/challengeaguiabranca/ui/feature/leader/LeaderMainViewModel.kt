package br.com.fiap.challengeaguiabranca.ui.feature.leader

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.fiap.challengeaguiabranca.domain.model.ManagerNotification
import br.com.fiap.challengeaguiabranca.domain.model.OperatorActivity
import br.com.fiap.challengeaguiabranca.domain.usecase.manager.GetMonthlyOperatorRankingUseCase
import br.com.fiap.challengeaguiabranca.domain.usecase.manager.GetOperatorActivityRankingUseCase
import br.com.fiap.challengeaguiabranca.domain.usecase.notification.ObserveManagementNotificationsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LeaderMainUiState(
    val notifications: List<ManagerNotification> = emptyList(),
    val hasUnreadNotifications: Boolean = false,
    val monthlyRanking: List<OperatorActivity> = emptyList(),
    val overallRanking: List<OperatorActivity> = emptyList()
)

class LeaderMainViewModel(
    observeManagementNotificationsUseCase: ObserveManagementNotificationsUseCase,
    getOperatorActivityRankingUseCase: GetOperatorActivityRankingUseCase,
    getMonthlyOperatorRankingUseCase: GetMonthlyOperatorRankingUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(LeaderMainUiState())
    val uiState = _uiState.asStateFlow()

    private val _overlay = MutableStateFlow(LeaderOverlay.NONE)
    val overlay = _overlay.asStateFlow()

    private var notificationsMarkedRead = false
    private var lastNotificationSignature = ""

    init {
        viewModelScope.launch {
            combine(
                observeManagementNotificationsUseCase(),
                getOperatorActivityRankingUseCase(),
                getMonthlyOperatorRankingUseCase()
            ) { notifications, overall, monthly ->
                val signature = notifications.joinToString { it.id }
                if (signature != lastNotificationSignature) {
                    if (lastNotificationSignature.isNotEmpty()) {
                        notificationsMarkedRead = false
                    }
                    lastNotificationSignature = signature
                }
                LeaderMainUiState(
                    notifications = notifications,
                    hasUnreadNotifications = !notificationsMarkedRead && notifications.isNotEmpty(),
                    monthlyRanking = monthly,
                    overallRanking = overall
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun markNotificationsAsRead() {
        notificationsMarkedRead = true
        _uiState.update { it.copy(hasUnreadNotifications = false) }
    }

    fun openSuggestion() {
        _overlay.value = LeaderOverlay.SUGGESTION
    }

    fun openCollaborators() {
        _overlay.value = LeaderOverlay.COLLABORATORS
    }

    fun closeOverlay() {
        _overlay.value = LeaderOverlay.NONE
    }
}
