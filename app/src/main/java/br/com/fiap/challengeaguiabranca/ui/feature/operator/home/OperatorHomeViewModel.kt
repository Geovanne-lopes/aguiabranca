package br.com.fiap.challengeaguiabranca.ui.feature.operator.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.fiap.challengeaguiabranca.domain.model.Idea
import br.com.fiap.challengeaguiabranca.domain.model.IdeaStatus
import br.com.fiap.challengeaguiabranca.domain.model.StrategicGuideline
import br.com.fiap.challengeaguiabranca.domain.usecase.guideline.ObserveGuidelinesUseCase
import br.com.fiap.challengeaguiabranca.domain.usecase.idea.ObserveIdeasByAuthorUseCase
import br.com.fiap.challengeaguiabranca.domain.usecase.insight.FetchDailyInsightUseCase
import br.com.fiap.challengeaguiabranca.domain.usecase.manager.ObserveSuggestionsForUserUseCase
import br.com.fiap.challengeaguiabranca.domain.usecase.session.ClearSessionUseCase
import br.com.fiap.challengeaguiabranca.domain.usecase.session.ObserveCurrentUserUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import br.com.fiap.challengeaguiabranca.domain.model.IdeaCategory
import br.com.fiap.challengeaguiabranca.domain.util.OperatorNotificationsBuilder
import br.com.fiap.challengeaguiabranca.ui.util.OperatorGamification
import kotlinx.coroutines.launch

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class OperatorHomeViewModel(
    observeCurrentUserUseCase: ObserveCurrentUserUseCase,
    private val observeIdeasByAuthorUseCase: ObserveIdeasByAuthorUseCase,
    private val observeGuidelinesUseCase: ObserveGuidelinesUseCase,
    private val fetchDailyInsightUseCase: FetchDailyInsightUseCase,
    private val observeSuggestionsForUserUseCase: ObserveSuggestionsForUserUseCase,
    private val clearSessionUseCase: ClearSessionUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(OperatorHomeUiState())
    val uiState = _uiState.asStateFlow()

    private val _selectedTab = MutableStateFlow(OperatorTab.HOME)
    val selectedTab = _selectedTab.asStateFlow()

    private val _overlay = MutableStateFlow(OperatorOverlay.NONE)
    val overlay = _overlay.asStateFlow()

    private val _submitOverlay = MutableStateFlow(SubmitIdeaOverlayState())
    val submitOverlay = _submitOverlay.asStateFlow()

    private var notificationsMarkedRead = false
    private var lastNotificationSignature = ""

    init {
        viewModelScope.launch {
            observeCurrentUserUseCase()
                .flatMapLatest { user ->
                    if (user == null) {
                        flowOf(OperatorHomeUiState(isLoading = false))
                    } else {
                        combine(
                            observeIdeasByAuthorUseCase(user.id),
                            observeGuidelinesUseCase(),
                            observeSuggestionsForUserUseCase(user.email, user.id)
                        ) { ideas, guidelines, suggestions ->
                            buildHomeState(
                                fullName = user.name,
                                email = user.email,
                                ideas = ideas,
                                guidelines = guidelines,
                                suggestions = suggestions
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
        if (tab != OperatorTab.HOME) {
            closeOverlay()
        }
    }

    fun markNotificationsAsRead() {
        notificationsMarkedRead = true
        _uiState.update { state ->
            state.copy(hasUnreadNotifications = false)
        }
    }

    fun openAllIdeas() {
        _overlay.value = OperatorOverlay.ALL_IDEAS
    }

    fun openSubmitIdea(category: IdeaCategory) {
        _submitOverlay.value = SubmitIdeaOverlayState(category = category)
        _overlay.value = OperatorOverlay.SUBMIT_IDEA
    }

    fun openCollaborators() {
        _overlay.value = OperatorOverlay.COLLABORATORS
    }

    fun closeOverlay() {
        _overlay.value = OperatorOverlay.NONE
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
        guidelines: List<StrategicGuideline>,
        suggestions: List<br.com.fiap.challengeaguiabranca.domain.model.ManagerSuggestion> = emptyList()
    ): OperatorHomeUiState {
        val firstName = fullName.substringBefore(" ").ifBlank { fullName }
        val submitted = ideas.size
        val approved = ideas.count { it.status == IdeaStatus.APPROVED }
        val gamification = OperatorGamification.fromIdeasCount(submitted)
        val notifications = OperatorNotificationsBuilder.build(guidelines, ideas, suggestions)
        val signature = notifications.joinToString { it.id }
        if (signature != lastNotificationSignature) {
            if (lastNotificationSignature.isNotEmpty()) {
                notificationsMarkedRead = false
            }
            lastNotificationSignature = signature
        }
        val sortedIdeas = ideas.sortedByDescending { it.createdAtEpochMillis }
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
            allIdeas = sortedIdeas,
            recentIdeas = sortedIdeas.take(3),
            notifications = notifications,
            hasUnreadNotifications = !notificationsMarkedRead && notifications.isNotEmpty(),
            isLoading = false
        )
    }
}
