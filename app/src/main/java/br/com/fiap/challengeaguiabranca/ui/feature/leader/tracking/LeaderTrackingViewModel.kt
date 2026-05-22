package br.com.fiap.challengeaguiabranca.ui.feature.leader.tracking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.fiap.challengeaguiabranca.domain.model.Project
import br.com.fiap.challengeaguiabranca.domain.usecase.project.ObserveAllProjectsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LeaderTrackingViewModel(
    observeAllProjectsUseCase: ObserveAllProjectsUseCase
) : ViewModel() {

    private val _projects = MutableStateFlow<List<Project>>(emptyList())
    val projects = _projects.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    init {
        viewModelScope.launch {
            observeAllProjectsUseCase().collect { list ->
                _projects.value = list.sortedByDescending { it.updatedAtEpochMillis }
                _isLoading.value = false
            }
        }
    }
}
