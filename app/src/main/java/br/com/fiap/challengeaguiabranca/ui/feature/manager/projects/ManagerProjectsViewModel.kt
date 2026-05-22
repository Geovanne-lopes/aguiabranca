package br.com.fiap.challengeaguiabranca.ui.feature.manager.projects

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.fiap.challengeaguiabranca.domain.model.Idea
import br.com.fiap.challengeaguiabranca.domain.model.IdeaStatus
import br.com.fiap.challengeaguiabranca.domain.model.Project
import br.com.fiap.challengeaguiabranca.domain.model.ProjectStatus
import br.com.fiap.challengeaguiabranca.domain.usecase.idea.ObserveAllIdeasUseCase
import br.com.fiap.challengeaguiabranca.domain.usecase.project.CreateProjectFromIdeaUseCase
import br.com.fiap.challengeaguiabranca.domain.usecase.project.DeleteProjectUseCase
import br.com.fiap.challengeaguiabranca.domain.usecase.project.ObserveAllProjectsUseCase
import br.com.fiap.challengeaguiabranca.domain.usecase.project.UpdateProjectUseCase
import br.com.fiap.challengeaguiabranca.domain.usecase.session.ObserveCurrentUserUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.ZoneId
import java.time.ZonedDateTime

class ManagerProjectsViewModel(
    observeCurrentUserUseCase: ObserveCurrentUserUseCase,
    observeAllProjectsUseCase: ObserveAllProjectsUseCase,
    observeAllIdeasUseCase: ObserveAllIdeasUseCase,
    private val createProjectFromIdeaUseCase: CreateProjectFromIdeaUseCase,
    private val updateProjectUseCase: UpdateProjectUseCase,
    private val deleteProjectUseCase: DeleteProjectUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ManagerProjectsUiState())
    val uiState = _uiState.asStateFlow()

    private var managerId: String? = null
    private var ideasCache: List<Idea> = emptyList()
    private var projectsCache: List<Project> = emptyList()

    init {
        viewModelScope.launch {
            observeCurrentUserUseCase().collect { user ->
                managerId = user?.id
            }
        }
        viewModelScope.launch {
            combine(
                observeAllProjectsUseCase(),
                observeAllIdeasUseCase()
            ) { projects, ideas ->
                projects to ideas
            }.collect { (projects, ideas) ->
                projectsCache = projects
                ideasCache = ideas
                val linkedIdeaIds = projects.map { it.ideaId }.toSet()
                val eligible = ideas.filter {
                    (it.status == IdeaStatus.APPROVED || it.status == IdeaStatus.PRIORITIZED) &&
                        it.id !in linkedIdeaIds
                }
                _uiState.update {
                    it.copy(
                        projects = projects.sortedByDescending { p -> p.updatedAtEpochMillis },
                        eligibleIdeas = eligible,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun openCreateDialog() {
        _uiState.update {
            it.copy(
                createDialogVisible = true,
                selectedIdeaId = it.eligibleIdeas.firstOrNull()?.id,
                message = null
            )
        }
    }

    fun closeCreateDialog() {
        _uiState.update { it.copy(createDialogVisible = false, selectedIdeaId = null) }
    }

    fun selectIdeaForCreate(ideaId: String) {
        _uiState.update { it.copy(selectedIdeaId = ideaId) }
    }

    fun createProjectFromSelectedIdea() {
        val ideaId = _uiState.value.selectedIdeaId
        val manager = managerId
        val idea = ideasCache.find { it.id == ideaId }
        when {
            manager == null -> showMessage("Sessão inválida.")
            idea == null -> showMessage("Selecione uma ideia.")
            else -> viewModelScope.launch {
                _uiState.update { it.copy(isSaving = true) }
                runCatching { createProjectFromIdeaUseCase(idea, manager) }
                    .onSuccess {
                        _uiState.update {
                            it.copy(
                                isSaving = false,
                                createDialogVisible = false,
                                selectedIdeaId = null,
                                message = "Projeto criado com sucesso."
                            )
                        }
                    }
                    .onFailure { error ->
                        _uiState.update {
                            it.copy(isSaving = false, message = error.message ?: "Erro ao criar projeto.")
                        }
                    }
            }
        }
    }

    fun openEdit(project: Project) {
        val daysText = project.deadlineEpochMillis?.let { deadline ->
            val now = ZonedDateTime.now(ZoneId.systemDefault()).toLocalDate()
            val end = InstantCompat.toLocalDate(deadline)
            (end.toEpochDay() - now.toEpochDay()).coerceAtLeast(0).toString()
        }.orEmpty()
        _uiState.update {
            it.copy(
                editForm = ProjectEditForm(
                    isVisible = true,
                    projectId = project.id,
                    title = project.title,
                    description = project.description,
                    status = project.status,
                    investmentText = project.investmentAmount.toString(),
                    profitText = project.obtainedProfit.toString(),
                    productivityText = project.productivityGainPercent.toString(),
                    deadlineDaysText = daysText
                ),
                message = null
            )
        }
    }

    fun closeEdit() {
        _uiState.update { it.copy(editForm = ProjectEditForm()) }
    }

    fun onEditStatusChange(status: ProjectStatus) {
        _uiState.update { state ->
            state.copy(editForm = state.editForm.copy(status = status))
        }
    }

    fun onEditInvestmentChange(value: String) {
        _uiState.update { it.copy(editForm = it.editForm.copy(investmentText = value)) }
    }

    fun onEditProfitChange(value: String) {
        _uiState.update { it.copy(editForm = it.editForm.copy(profitText = value)) }
    }

    fun onEditProductivityChange(value: String) {
        _uiState.update { it.copy(editForm = it.editForm.copy(productivityText = value)) }
    }

    fun onEditDeadlineDaysChange(value: String) {
        _uiState.update { it.copy(editForm = it.editForm.copy(deadlineDaysText = value)) }
    }

    fun saveEdit() {
        val form = _uiState.value.editForm
        val project = projectsCache.find { it.id == form.projectId }
        if (project == null) {
            showMessage("Projeto não encontrado.")
            return
        }
        val investment = form.investmentText.replace(",", ".").toDoubleOrNull()
        val profit = form.profitText.replace(",", ".").toDoubleOrNull()
        val productivity = form.productivityText.replace(",", ".").toDoubleOrNull()
        when {
            investment == null || investment < 0 ->
                showMessage("Informe um investimento válido.")
            profit == null || profit < 0 ->
                showMessage("Informe um lucro obtido válido.")
            productivity == null || productivity < 0 || productivity > 100 ->
                showMessage("Produtividade deve ser entre 0 e 100.")
            else -> viewModelScope.launch {
                _uiState.update { it.copy(isSaving = true) }
                val deadline = parseDeadlineDays(form.deadlineDaysText)
                val updated = project.copy(
                    status = form.status,
                    investmentAmount = investment,
                    obtainedProfit = profit,
                    productivityGainPercent = productivity,
                    deadlineEpochMillis = deadline
                )
                runCatching { updateProjectUseCase(updated) }
                    .onSuccess {
                        _uiState.update {
                            it.copy(
                                isSaving = false,
                                editForm = ProjectEditForm(),
                                message = "Projeto atualizado."
                            )
                        }
                    }
                    .onFailure { error ->
                        _uiState.update {
                            it.copy(
                                isSaving = false,
                                message = error.message ?: "Erro ao salvar."
                            )
                        }
                    }
            }
        }
    }

    fun deleteProject(projectId: String) {
        viewModelScope.launch {
            runCatching { deleteProjectUseCase(projectId) }
                .onSuccess { _uiState.update { it.copy(message = "Projeto removido.") } }
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

    private fun parseDeadlineDays(text: String): Long? {
        val days = text.trim().toLongOrNull() ?: return null
        if (days <= 0) return null
        return ZonedDateTime.now(ZoneId.systemDefault())
            .plusDays(days)
            .toInstant()
            .toEpochMilli()
    }
}

private object InstantCompat {
    fun toLocalDate(epochMillis: Long) =
        java.time.Instant.ofEpochMilli(epochMillis)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
}
