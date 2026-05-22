package br.com.fiap.challengeaguiabranca.ui.feature.manager.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.fiap.challengeaguiabranca.domain.model.IdeaStatus
import br.com.fiap.challengeaguiabranca.domain.usecase.idea.ObserveAllIdeasUseCase
import br.com.fiap.challengeaguiabranca.domain.usecase.project.ObserveAllProjectsUseCase
import br.com.fiap.challengeaguiabranca.domain.usecase.session.ObserveCurrentUserUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class ManagerDashboardViewModel(
    observeCurrentUserUseCase: ObserveCurrentUserUseCase,
    observeAllIdeasUseCase: ObserveAllIdeasUseCase,
    observeAllProjectsUseCase: ObserveAllProjectsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ManagerDashboardUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            observeCurrentUserUseCase()
                .flatMapLatest { user ->
                    if (user == null) {
                        flowOf(ManagerDashboardUiState(isLoading = false))
                    } else {
                        combine(
                            observeAllIdeasUseCase(),
                            observeAllProjectsUseCase()
                        ) { ideas, projects ->
                            val ideasThisMonth = ManagerDashboardStats.ideasThisMonth(ideas)
                            val ideasPrevMonth = ManagerDashboardStats.ideasPreviousMonth(ideas)
                            val rate = ManagerDashboardStats.approvalRatePercent(ideas)
                            ManagerDashboardUiState(
                                userFirstName = user.name.substringBefore(" ").ifBlank { user.name },
                                pendingIdeasCount = ideas.count { it.status == IdeaStatus.PENDING },
                                activeProjectsCount = ManagerDashboardStats.activeProjectsCount(projects),
                                ideasReceivedCount = ideasThisMonth,
                                approvalRatePercent = rate,
                                ideasReceivedTrend = ManagerDashboardStats.monthComparisonTrend(
                                    ideasThisMonth,
                                    ideasPrevMonth
                                ),
                                approvalRateTrend = if (ideas.isEmpty()) {
                                    "Sem ideias para calcular"
                                } else {
                                    "$rate% no total"
                                },
                                monthlyBars = ManagerDashboardStats.buildMonthlyBars(ideas),
                                isLoading = false
                            )
                        }
                    }
                }
                .collect { state ->
                    _uiState.value = state
                }
        }
    }
}
