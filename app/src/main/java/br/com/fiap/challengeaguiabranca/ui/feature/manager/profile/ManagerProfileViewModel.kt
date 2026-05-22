package br.com.fiap.challengeaguiabranca.ui.feature.manager.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.fiap.challengeaguiabranca.domain.model.IdeaStatus
import br.com.fiap.challengeaguiabranca.domain.model.ProjectStatus
import br.com.fiap.challengeaguiabranca.domain.usecase.idea.ObserveAllIdeasUseCase
import br.com.fiap.challengeaguiabranca.domain.usecase.project.ObserveAllProjectsUseCase
import br.com.fiap.challengeaguiabranca.domain.usecase.session.ObserveCurrentUserUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

data class ManagerProfileUiState(
    val userName: String = "",
    val userEmail: String = "",
    val pendingIdeas: Int = 0,
    val activeProjects: Int = 0,
    val totalIdeas: Int = 0,
    val isLoading: Boolean = true
)

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class ManagerProfileViewModel(
    observeCurrentUserUseCase: ObserveCurrentUserUseCase,
    observeAllIdeasUseCase: ObserveAllIdeasUseCase,
    observeAllProjectsUseCase: ObserveAllProjectsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ManagerProfileUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            observeCurrentUserUseCase()
                .flatMapLatest { user ->
                    if (user == null) {
                        flowOf(ManagerProfileUiState(isLoading = false))
                    } else {
                        combine(
                            observeAllIdeasUseCase(),
                            observeAllProjectsUseCase()
                        ) { ideas, projects ->
                            ManagerProfileUiState(
                                userName = user.name,
                                userEmail = user.email,
                                pendingIdeas = ideas.count { it.status == IdeaStatus.PENDING },
                                activeProjects = projects.count { it.status != ProjectStatus.COMPLETED },
                                totalIdeas = ideas.size,
                                isLoading = false
                            )
                        }
                    }
                }
                .collect { state -> _uiState.value = state }
        }
    }
}
