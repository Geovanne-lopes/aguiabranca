package br.com.fiap.challengeaguiabranca.ui.feature.operator.ideas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.fiap.challengeaguiabranca.domain.model.Idea
import br.com.fiap.challengeaguiabranca.domain.model.IdeaCategory
import br.com.fiap.challengeaguiabranca.domain.model.IdeaStatus
import br.com.fiap.challengeaguiabranca.domain.usecase.guideline.ObserveGuidelinesUseCase
import br.com.fiap.challengeaguiabranca.domain.usecase.idea.DeleteIdeaUseCase
import br.com.fiap.challengeaguiabranca.domain.usecase.idea.ObserveIdeasByAuthorUseCase
import br.com.fiap.challengeaguiabranca.domain.usecase.idea.SubmitIdeaUseCase
import br.com.fiap.challengeaguiabranca.domain.usecase.idea.UpdateIdeaUseCase
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
    observeGuidelinesUseCase: ObserveGuidelinesUseCase,
    private val submitIdeaUseCase: SubmitIdeaUseCase,
    private val updateIdeaUseCase: UpdateIdeaUseCase,
    private val deleteIdeaUseCase: DeleteIdeaUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(OperatorIdeasUiState())
    val uiState = _uiState.asStateFlow()

    private var authorId: String? = null
    private var ideasCache: List<Idea> = emptyList()

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
                    ideasCache = ideas
                    _uiState.update {
                        it.copy(ideas = ideas, isLoading = false)
                    }
                }
        }
        viewModelScope.launch {
            observeGuidelinesUseCase().collect { guidelines ->
                _uiState.update { it.copy(guidelines = guidelines) }
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

    fun openEdit(idea: Idea) {
        if (idea.status != IdeaStatus.PENDING) return
        _uiState.update {
            it.copy(
                form = IdeaFormState(
                    isVisible = true,
                    editingId = idea.id,
                    title = idea.title,
                    description = idea.description,
                    category = idea.category,
                    guidelineId = idea.guidelineId
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

    fun onGuidelineChange(guidelineId: String) {
        _uiState.update { state ->
            state.copy(form = state.form.copy(guidelineId = guidelineId), errorMessage = null)
        }
    }

    fun requestDelete(idea: Idea) {
        if (idea.status != IdeaStatus.PENDING) return
        _uiState.update { it.copy(pendingDeleteId = idea.id, errorMessage = null) }
    }

    fun dismissDelete() {
        _uiState.update { it.copy(pendingDeleteId = null) }
    }

    fun confirmDelete() {
        val ideaId = _uiState.value.pendingDeleteId ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, errorMessage = null) }
            runCatching { deleteIdeaUseCase(ideaId) }
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            isSubmitting = false,
                            pendingDeleteId = null,
                            successMessage = "Ideia excluída."
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isSubmitting = false,
                            pendingDeleteId = null,
                            errorMessage = error.message ?: "Erro ao excluir ideia."
                        )
                    }
                }
        }
    }

    fun submitIdea() {
        val form = _uiState.value.form
        val userId = authorId
        val guidelines = _uiState.value.guidelines
        when {
            userId == null ->
                _uiState.update { it.copy(errorMessage = "Sessão inválida. Faça login novamente.") }
            guidelines.isEmpty() ->
                _uiState.update { it.copy(errorMessage = "A liderança ainda não publicou uma estratégia.") }
            form.guidelineId.isNullOrBlank() ->
                _uiState.update { it.copy(errorMessage = "Selecione a estratégia vigente.") }
            form.title.trim().length < 3 ->
                _uiState.update { it.copy(errorMessage = "Título deve ter pelo menos 3 caracteres.") }
            form.description.trim().length < 10 ->
                _uiState.update { it.copy(errorMessage = "Descrição deve ter pelo menos 10 caracteres.") }
            else -> viewModelScope.launch {
                _uiState.update { it.copy(isSubmitting = true, errorMessage = null) }
                val editingId = form.editingId
                val result = if (editingId == null) {
                    runCatching {
                        submitIdeaUseCase(
                            title = form.title,
                            description = form.description,
                            category = form.category,
                            authorId = userId,
                            guidelineId = form.guidelineId
                        )
                    }
                } else {
                    val existing = ideasCache.find { it.id == editingId }
                    if (existing == null || existing.status != IdeaStatus.PENDING) {
                        Result.failure(IllegalStateException("Só é possível alterar uma ideia pendente."))
                    } else {
                        runCatching {
                            updateIdeaUseCase(
                                existing.copy(
                                    title = form.title.trim(),
                                    description = form.description.trim(),
                                    category = form.category,
                                    guidelineId = form.guidelineId
                                )
                            )
                        }
                    }
                }
                result
                    .onSuccess {
                        _uiState.update {
                            it.copy(
                                isSubmitting = false,
                                form = IdeaFormState(isVisible = false),
                                successMessage = if (editingId == null) {
                                    "Ideia enviada com sucesso!"
                                } else {
                                    "Ideia atualizada."
                                }
                            )
                        }
                    }
                    .onFailure { error ->
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
