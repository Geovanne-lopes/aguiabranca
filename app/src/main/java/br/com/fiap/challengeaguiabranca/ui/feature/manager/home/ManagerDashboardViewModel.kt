package br.com.fiap.challengeaguiabranca.ui.feature.manager.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.fiap.challengeaguiabranca.domain.usecase.dashboard.ObserveManagerDashboardUseCase
import br.com.fiap.challengeaguiabranca.domain.usecase.session.ObserveCurrentUserUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class ManagerDashboardViewModel(
    observeCurrentUserUseCase: ObserveCurrentUserUseCase,
    observeManagerDashboardUseCase: ObserveManagerDashboardUseCase
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
                        observeManagerDashboardUseCase().map { dashboard ->
                            ManagerDashboardUiState(
                                userFirstName = user.name.substringBefore(" ").ifBlank { user.name },
                                pendingIdeasCount = dashboard.pendingIdeasCount,
                                activeProjectsCount = dashboard.activeProjectsCount,
                                ideasReceivedCount = dashboard.ideasReceivedThisMonth,
                                approvalRatePercent = dashboard.approvalRatePercent,
                                ideasReceivedTrend = dashboard.ideasReceivedTrendLabel,
                                approvalRateTrend = dashboard.approvalRateTrendLabel,
                                monthlyBars = dashboard.monthlyBars.map { MonthBarData(it.label, it.count) },
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
