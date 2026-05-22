package br.com.fiap.challengeaguiabranca.ui.feature.leader.guidelines

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.fiap.challengeaguiabranca.domain.model.StrategicGuideline
import br.com.fiap.challengeaguiabranca.domain.usecase.guideline.CreateGuidelineUseCase
import br.com.fiap.challengeaguiabranca.domain.usecase.guideline.DeleteGuidelineUseCase
import br.com.fiap.challengeaguiabranca.domain.usecase.guideline.ObserveGuidelinesUseCase
import br.com.fiap.challengeaguiabranca.domain.usecase.guideline.UpdateGuidelineUseCase
import br.com.fiap.challengeaguiabranca.domain.usecase.session.ObserveCurrentUserUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LeaderGuidelinesViewModel(
    observeCurrentUserUseCase: ObserveCurrentUserUseCase,
    observeGuidelinesUseCase: ObserveGuidelinesUseCase,
    private val createGuidelineUseCase: CreateGuidelineUseCase,
    private val updateGuidelineUseCase: UpdateGuidelineUseCase,
    private val deleteGuidelineUseCase: DeleteGuidelineUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(LeaderGuidelinesUiState())
    val uiState = _uiState.asStateFlow()

    private var authorId: String? = null
    private var guidelinesCache: List<StrategicGuideline> = emptyList()

    init {
        viewModelScope.launch {
            observeCurrentUserUseCase().collect { user ->
                authorId = user?.id
            }
        }
        viewModelScope.launch {
            observeGuidelinesUseCase().collect { list ->
                guidelinesCache = list
                _uiState.update {
                    it.copy(guidelines = list.sortedByDescending { g -> g.updatedAtEpochMillis }, isLoading = false)
                }
            }
        }
    }

    fun openCreateForm() {
        _uiState.update {
            it.copy(form = GuidelineFormState(isVisible = true), message = null)
        }
    }

    fun openEditForm(guideline: StrategicGuideline) {
        _uiState.update {
            it.copy(
                form = GuidelineFormState(
                    isVisible = true,
                    editingId = guideline.id,
                    title = guideline.title,
                    content = guideline.content
                ),
                message = null
            )
        }
    }

    fun closeForm() {
        _uiState.update { it.copy(form = GuidelineFormState()) }
    }

    fun onTitleChange(value: String) {
        _uiState.update { it.copy(form = it.form.copy(title = value)) }
    }

    fun onContentChange(value: String) {
        _uiState.update { it.copy(form = it.form.copy(content = value)) }
    }

    fun saveForm() {
        val form = _uiState.value.form
        val userId = authorId
        when {
            userId == null -> showMessage("Sessão inválida.")
            form.title.trim().length < 3 -> showMessage("Título deve ter pelo menos 3 caracteres.")
            form.content.trim().length < 10 -> showMessage("Conteúdo deve ter pelo menos 10 caracteres.")
            else -> viewModelScope.launch {
                _uiState.update { it.copy(isSaving = true) }
                val editingId = form.editingId
                val result = if (editingId == null) {
                    runCatching {
                        createGuidelineUseCase(form.title, form.content, userId)
                    }
                } else {
                    val existing = guidelinesCache.find { it.id == editingId }
                    if (existing == null) {
                        Result.failure(IllegalStateException("Diretriz não encontrada."))
                    } else {
                        runCatching {
                            updateGuidelineUseCase(
                                existing.copy(title = form.title.trim(), content = form.content.trim())
                            )
                        }
                    }
                }
                result
                    .onSuccess {
                        _uiState.update {
                            it.copy(
                                isSaving = false,
                                form = GuidelineFormState(),
                                message = if (editingId == null) "Diretriz criada." else "Diretriz atualizada."
                            )
                        }
                    }
                    .onFailure { error ->
                        _uiState.update {
                            it.copy(isSaving = false, message = error.message ?: "Erro ao salvar.")
                        }
                    }
            }
        }
    }

    fun deleteGuideline(id: String) {
        viewModelScope.launch {
            runCatching { deleteGuidelineUseCase(id) }
                .onSuccess { _uiState.update { it.copy(message = "Diretriz removida.") } }
                .onFailure { error ->
                    _uiState.update { it.copy(message = error.message ?: "Erro ao remover.") }
                }
        }
    }

    fun clearMessage() {
        _uiState.update { it.copy(message = null) }
    }

    private fun showMessage(text: String) {
        _uiState.update { it.copy(message = text) }
    }
}
