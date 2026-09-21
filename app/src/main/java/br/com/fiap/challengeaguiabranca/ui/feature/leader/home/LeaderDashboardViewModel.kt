package br.com.fiap.challengeaguiabranca.ui.feature.leader.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.fiap.challengeaguiabranca.domain.usecase.ai.GenerateAiInsightUseCase
import br.com.fiap.challengeaguiabranca.domain.usecase.ai.GetLatestAiInsightUseCase
import br.com.fiap.challengeaguiabranca.domain.usecase.dashboard.ObserveLeaderDashboardUseCase
import br.com.fiap.challengeaguiabranca.domain.usecase.dashboard.ObserveStrategyReturnsUseCase
import br.com.fiap.challengeaguiabranca.domain.usecase.session.ObserveCurrentUserUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class LeaderDashboardViewModel(
    observeCurrentUserUseCase: ObserveCurrentUserUseCase,
    observeLeaderDashboardUseCase: ObserveLeaderDashboardUseCase,
    observeStrategyReturnsUseCase: ObserveStrategyReturnsUseCase,
    private val getLatestAiInsightUseCase: GetLatestAiInsightUseCase,
    private val generateAiInsightUseCase: GenerateAiInsightUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(LeaderDashboardUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            observeCurrentUserUseCase()
                .flatMapLatest { user ->
                    if (user == null) {
                        flowOf(LeaderDashboardUiState(isLoading = false))
                    } else {
                        combine(
                            observeLeaderDashboardUseCase(),
                            observeStrategyReturnsUseCase()
                        ) { dashboard, strategies ->
                            LeaderDashboardUiState(
                                userFirstName = user.name.substringBefore(" ").ifBlank { user.name },
                                summary = dashboard.summary,
                                statusChartLabels = dashboard.statusChartLabels,
                                statusChartValues = dashboard.statusChartValues,
                                strategyReturns = strategies,
                                isLoading = false
                            )
                        }
                    }
                }
                .collect { incoming ->
                    _uiState.update { current ->
                        incoming.copy(
                            aiInsightText = current.aiInsightText,
                            aiInsightDisclaimer = current.aiInsightDisclaimer,
                            aiInsightLoading = current.aiInsightLoading,
                            aiInsightError = current.aiInsightError
                        )
                    }
                }
        }
        viewModelScope.launch {
            runCatching { getLatestAiInsightUseCase() }
                .onSuccess { insight ->
                    if (insight != null) {
                        _uiState.update {
                            it.copy(
                                aiInsightText = insight.content,
                                aiInsightDisclaimer = insight.disclaimer
                            )
                        }
                    }
                }
        }
    }

    fun generateAiInsight() {
        viewModelScope.launch {
            _uiState.update { it.copy(aiInsightLoading = true, aiInsightError = null) }
            runCatching { generateAiInsightUseCase() }
                .onSuccess { insight ->
                    _uiState.update {
                        it.copy(
                            aiInsightLoading = false,
                            aiInsightText = insight.content,
                            aiInsightDisclaimer = insight.disclaimer,
                            aiInsightError = null
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            aiInsightLoading = false,
                            aiInsightError = error.message ?: "Não foi possível gerar o insight."
                        )
                    }
                }
        }
    }

    fun clearAiInsightError() {
        _uiState.update { it.copy(aiInsightError = null) }
    }
}
