package br.com.fiap.challengeaguiabranca.ui.feature.leader.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.fiap.challengeaguiabranca.domain.model.ProjectStatus
import br.com.fiap.challengeaguiabranca.domain.model.RoiDashboardSummary
import br.com.fiap.challengeaguiabranca.domain.usecase.project.ObserveAllProjectsUseCase
import br.com.fiap.challengeaguiabranca.domain.usecase.session.ObserveCurrentUserUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class LeaderDashboardViewModel(
    observeCurrentUserUseCase: ObserveCurrentUserUseCase,
    observeAllProjectsUseCase: ObserveAllProjectsUseCase
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
                        observeAllProjectsUseCase().map { projects ->
                            val summary = RoiDashboardSummary.fromProjects(projects)
                            val statusCounts = ProjectStatus.entries.map { status ->
                                status to projects.count { it.status == status }
                            }
                            LeaderDashboardUiState(
                                userFirstName = user.name.substringBefore(" ").ifBlank { user.name },
                                summary = summary,
                                statusChartLabels = statusCounts.map { it.first.label },
                                statusChartValues = statusCounts.map { it.second },
                                isLoading = false
                            )
                        }
                    }
                }
                .collect { state -> _uiState.value = state }
        }
    }
}
