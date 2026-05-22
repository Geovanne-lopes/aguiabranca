package br.com.fiap.challengeaguiabranca.ui.feature.operator.ideas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.fiap.challengeaguiabranca.domain.model.IdeaCategory
import br.com.fiap.challengeaguiabranca.domain.usecase.idea.ObserveIdeasByAuthorUseCase
import br.com.fiap.challengeaguiabranca.domain.usecase.idea.SubmitIdeaUseCase
import br.com.fiap.challengeaguiabranca.domain.usecase.session.ObserveCurrentUserUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class OperatorIdeasViewModel(
    observeCurrentUserUseCase: ObserveCurrentUserUseCase,
    private val observeIdeasByAuthorUseCase: ObserveIdeasByAuthorUseCase,
    private val submitIdeaUseCase: SubmitIdeaUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(OperatorIdeasUiState())
    val uiState = _uiState.asStateFlow()

    private var authorId: String? = null

    init {
        viewModelScope.launch {
            observeCurrentUserUseCase()
                .flatMapLatest { user ->
                    authorId = user?.id
                    if (user == null) {
                        flowOf(emptyList())
                    } else {
                        observeIdeasByAuthorUseCase(user.id)
                    }
                }
                .collect { ideas ->
                    _uiState.update {
                        it.copy(ideas = ideas, isLoading = false)
                    }
                }
        }
    }

    fun openForm(defaultCategory: IdeaCategory = IdeaCategory.PROCESS) {
        _uiState.update {
            it.copy(
                form = IdeaFormState(
                    isVisible = true,
                    category = defaultCategory
                ),
                errorMessage = null,
                successMessage = null
            )
        }
    }

    fun closeForm() {
        _uiState.update {
            it.copy(form = IdeaFormState(isVisible = false))
        }
    }

    fun onTitleChange(value: String) {
        _uiState.update { state ->
            state.copy(form = state.form.copy(title = value), errorMessage = null)
        }
    }

    fun onDescriptionChange(value: String) {
        _uiState.update { state ->
            state.copy(form = state.form.copy(description = value), errorMessage = null)
        }
    }

    fun onCategoryChange(category: IdeaCategory) {
        _uiState.update { state ->
            state.copy(form = state.form.copy(category = category), errorMessage = null)
        }
    }

    fun submitIdea() {
        val form = _uiState.value.form
        val userId = authorId
        when {
            userId == null ->
                _uiState.update { it.copy(errorMessage = "Sessão inválida. Faça login novamente.") }
            form.title.trim().length < 3 ->
                _uiState.update { it.copy(errorMessage = "Título deve ter pelo menos 3 caracteres.") }
            form.description.trim().length < 10 ->
                _uiState.update { it.copy(errorMessage = "Descrição deve ter pelo menos 10 caracteres.") }
            else -> viewModelScope.launch {
                _uiState.update { it.copy(isSubmitting = true, errorMessage = null) }
                runCatching {
                    submitIdeaUseCase(
                        title = form.title,
                        description = form.description,
                        category = form.category,
                        authorId = userId
                    )
                }.onSuccess {
                    _uiState.update {
                        it.copy(
                            isSubmitting = false,
                            form = IdeaFormState(isVisible = false),
                            successMessage = "Ideia enviada com sucesso!"
                        )
                    }
                }.onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isSubmitting = false,
                            errorMessage = error.message ?: "Erro ao enviar ideia."
                        )
                    }
                }
            }
        }
    }

    fun clearSuccessMessage() {
        _uiState.update { it.copy(successMessage = null) }
    }
}
