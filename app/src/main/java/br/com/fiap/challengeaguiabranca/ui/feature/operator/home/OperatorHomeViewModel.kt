package br.com.fiap.challengeaguiabranca.ui.feature.operator.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.fiap.challengeaguiabranca.domain.model.Idea
import br.com.fiap.challengeaguiabranca.domain.model.IdeaStatus
import br.com.fiap.challengeaguiabranca.domain.model.StrategicGuideline
import br.com.fiap.challengeaguiabranca.domain.usecase.guideline.ObserveGuidelinesUseCase
import br.com.fiap.challengeaguiabranca.domain.usecase.idea.ObserveIdeasByAuthorUseCase
import br.com.fiap.challengeaguiabranca.domain.usecase.insight.FetchDailyInsightUseCase
import br.com.fiap.challengeaguiabranca.domain.usecase.session.ClearSessionUseCase
import br.com.fiap.challengeaguiabranca.domain.usecase.session.ObserveCurrentUserUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import br.com.fiap.challengeaguiabranca.ui.util.OperatorGamification
import kotlinx.coroutines.launch

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class OperatorHomeViewModel(
    observeCurrentUserUseCase: ObserveCurrentUserUseCase,
    private val observeIdeasByAuthorUseCase: ObserveIdeasByAuthorUseCase,
    private val observeGuidelinesUseCase: ObserveGuidelinesUseCase,
    private val fetchDailyInsightUseCase: FetchDailyInsightUseCase,
    private val clearSessionUseCase: ClearSessionUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(OperatorHomeUiState())
    val uiState = _uiState.asStateFlow()

    private val _selectedTab = MutableStateFlow(OperatorTab.HOME)
    val selectedTab = _selectedTab.asStateFlow()

    init {
        viewModelScope.launch {
            observeCurrentUserUseCase()
                .flatMapLatest { user ->
                    if (user == null) {
                        flowOf(OperatorHomeUiState(isLoading = false))
                    } else {
                        combine(
                            observeIdeasByAuthorUseCase(user.id),
                            observeGuidelinesUseCase()
                        ) { ideas, guidelines ->
                            buildHomeState(
                                fullName = user.name,
                                email = user.email,
                                ideas = ideas,
                                guidelines = guidelines
                            )
                        }
                    }
                }
                .collect { state ->
                    _uiState.value = state
                }
        }
        loadInsight()
    }

    fun selectTab(tab: OperatorTab) {
        _selectedTab.value = tab
    }

    fun loadInsight() {
        viewModelScope.launch {
            _uiState.update { it.copy(insightLoading = true, insightError = null) }
            runCatching { fetchDailyInsightUseCase() }
                .onSuccess { insight ->
                    _uiState.update {
                        it.copy(insight = insight, insightLoading = false, insightError = null)
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            insightLoading = false,
                            insightError = error.message ?: "Não foi possível carregar o insight."
                        )
                    }
                }
        }
    }

    fun logout(onCompleted: () -> Unit) {
        viewModelScope.launch {
            clearSessionUseCase()
            onCompleted()
        }
    }

    private fun buildHomeState(
        fullName: String,
        email: String,
        ideas: List<Idea>,
        guidelines: List<StrategicGuideline>
    ): OperatorHomeUiState {
        val firstName = fullName.substringBefore(" ").ifBlank { fullName }
        val submitted = ideas.size
        val approved = ideas.count { it.status == IdeaStatus.APPROVED }
        val gamification = OperatorGamification.fromIdeasCount(submitted)
        return OperatorHomeUiState(
            userFirstName = firstName,
            userFullName = fullName,
            userEmail = email,
            gamification = gamification,
            gamificationLevel = gamification.level,
            gamificationPoints = gamification.points,
            ideasSubmittedCount = submitted,
            ideasApprovedCount = approved,
            submittedTrendLabel = OperatorKpiTrend.submittedTrend(ideas),
            approvedTrendLabel = OperatorKpiTrend.approvedTrend(ideas),
            allGuidelines = guidelines,
            featuredGuideline = guidelines.firstOrNull(),
            recentIdeas = ideas.take(3),
            isLoading = false
        )
    }
}
