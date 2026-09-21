package br.com.fiap.challengeaguiabranca.ui.feature.manager

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.fiap.challengeaguiabranca.domain.model.ManagerNotification
import br.com.fiap.challengeaguiabranca.domain.model.OperatorActivity
import br.com.fiap.challengeaguiabranca.domain.usecase.manager.GetOperatorActivityRankingUseCase
import br.com.fiap.challengeaguiabranca.domain.usecase.notification.ObserveManagementNotificationsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ManagerMainUiState(
    val notifications: List<ManagerNotification> = emptyList(),
    val hasUnreadNotifications: Boolean = false,
    val topOperators: List<OperatorActivity> = emptyList(),
    val allOperators: List<OperatorActivity> = emptyList()
)

class ManagerMainViewModel(
    observeManagementNotificationsUseCase: ObserveManagementNotificationsUseCase,
    getOperatorActivityRankingUseCase: GetOperatorActivityRankingUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ManagerMainUiState())
    val uiState = _uiState.asStateFlow()

    private val _overlay = MutableStateFlow(ManagerOverlay.NONE)
    val overlay = _overlay.asStateFlow()

    private var notificationsMarkedRead = false
    private var lastNotificationSignature = ""

    init {
        viewModelScope.launch {
            combine(
                observeManagementNotificationsUseCase(),
                getOperatorActivityRankingUseCase()
            ) { notifications, ranking ->
                val signature = notifications.joinToString { it.id }
                if (signature != lastNotificationSignature) {
                    if (lastNotificationSignature.isNotEmpty()) {
                        notificationsMarkedRead = false
                    }
                    lastNotificationSignature = signature
                }
                ManagerMainUiState(
                    notifications = notifications,
                    hasUnreadNotifications = !notificationsMarkedRead && notifications.isNotEmpty(),
                    topOperators = ranking.take(3),
                    allOperators = ranking
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

    fun openTeam() {
        _overlay.value = ManagerOverlay.TEAM
    }

    fun openSuggestion() {
        _overlay.value = ManagerOverlay.SUGGESTION
    }

    fun openCreateIdea() {
        _overlay.value = ManagerOverlay.CREATE_IDEA
    }

    fun openCollaborators() {
        _overlay.value = ManagerOverlay.COLLABORATORS
    }

    fun closeOverlay() {
        _overlay.value = ManagerOverlay.NONE
    }
}
