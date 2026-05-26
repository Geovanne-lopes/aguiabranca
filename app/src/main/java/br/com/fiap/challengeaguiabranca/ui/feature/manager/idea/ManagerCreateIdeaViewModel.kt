package br.com.fiap.challengeaguiabranca.ui.feature.manager.idea

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.fiap.challengeaguiabranca.domain.model.IdeaCategory
import br.com.fiap.challengeaguiabranca.domain.usecase.idea.SubmitIdeaUseCase
import br.com.fiap.challengeaguiabranca.domain.usecase.session.ObserveCurrentUserUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ManagerCreateIdeaUiState(
    val title: String = "",
    val description: String = "",
    val category: IdeaCategory = IdeaCategory.PROCESS,
    val isSubmitting: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

class ManagerCreateIdeaViewModel(
    observeCurrentUserUseCase: ObserveCurrentUserUseCase,
    private val submitIdeaUseCase: SubmitIdeaUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ManagerCreateIdeaUiState())
    val uiState = _uiState.asStateFlow()

    private var managerId: String? = null

    init {
        viewModelScope.launch {
            observeCurrentUserUseCase().collect { user ->
                managerId = user?.id
            }
        }
    }

    fun onTitleChange(value: String) {
        _uiState.update { it.copy(title = value, errorMessage = null) }
    }

    fun onDescriptionChange(value: String) {
        _uiState.update { it.copy(description = value, errorMessage = null) }
    }

    fun onCategoryChange(category: IdeaCategory) {
        _uiState.update { it.copy(category = category, errorMessage = null) }
    }

    fun submit() {
        val state = _uiState.value
        val id = managerId
        when {
            id == null -> _uiState.update { it.copy(errorMessage = "Sessão inválida.") }
            state.title.trim().length < 3 ->
                _uiState.update { it.copy(errorMessage = "Título deve ter pelo menos 3 caracteres.") }
            state.description.trim().length < 10 ->
                _uiState.update { it.copy(errorMessage = "Descrição deve ter pelo menos 10 caracteres.") }
            else -> viewModelScope.launch {
                _uiState.update { it.copy(isSending = true, errorMessage = null) }
                runCatching {
                    submitIdeaUseCase(
                        title = state.title,
                        description = state.description,
                        category = state.category,
                        authorId = id
                    )
                }.onSuccess {
                    _uiState.update {
                        it.copy(
                            isSending = false,
                            title = "",
                            description = "",
                            successMessage = "Ideia registrada com sucesso!"
                        )
                    }
                }.onFailure { error ->
                    _uiState.update {
                        it.copy(isSending = false, errorMessage = error.message ?: "Erro ao enviar.")
                    }
                }
            }
        }
    }

    fun clearSuccessMessage() {
        _uiState.update { it.copy(successMessage = null) }
    }
}
