package br.com.fiap.challengeaguiabranca.ui.feature.manager.curation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.fiap.challengeaguiabranca.domain.model.Idea
import br.com.fiap.challengeaguiabranca.domain.model.IdeaStatus
import br.com.fiap.challengeaguiabranca.domain.usecase.idea.ObserveAllIdeasUseCase
import br.com.fiap.challengeaguiabranca.domain.usecase.idea.UpdateIdeaStatusUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ManagerCurationViewModel(
    observeAllIdeasUseCase: ObserveAllIdeasUseCase,
    private val updateIdeaStatusUseCase: UpdateIdeaStatusUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ManagerCurationUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            observeAllIdeasUseCase().collect { ideas ->
                _uiState.update {
                    it.copy(ideas = ideas.sortedByDescending { idea -> idea.createdAtEpochMillis }, isLoading = false)
                }
            }
        }
    }

    fun updateStatus(idea: Idea, status: IdeaStatus) {
        viewModelScope.launch {
            _uiState.update { it.copy(actionMessage = null) }
            runCatching { updateIdeaStatusUseCase(idea.id, status) }
                .onSuccess {
                    _uiState.update {
                        it.copy(actionMessage = "Status atualizado: ${status.name}")
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(actionMessage = error.message ?: "Erro ao atualizar status.")
                    }
                }
        }
    }

    fun clearMessage() {
        _uiState.update { it.copy(actionMessage = null) }
    }
}

data class ManagerCurationUiState(
    val ideas: List<Idea> = emptyList(),
    val isLoading: Boolean = true,
    val actionMessage: String? = null
)
