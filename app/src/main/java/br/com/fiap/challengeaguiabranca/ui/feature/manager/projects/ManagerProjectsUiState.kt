package br.com.fiap.challengeaguiabranca.ui.feature.manager.projects

import br.com.fiap.challengeaguiabranca.domain.model.Idea
import br.com.fiap.challengeaguiabranca.domain.model.Project
import br.com.fiap.challengeaguiabranca.domain.model.ProjectStatus

data class ManagerProjectsUiState(
    val projects: List<Project> = emptyList(),
    val eligibleIdeas: List<Idea> = emptyList(),
    val isLoading: Boolean = true,
    val createDialogVisible: Boolean = false,
    val selectedIdeaId: String? = null,
    val editForm: ProjectEditForm = ProjectEditForm(),
    val isSaving: Boolean = false,
    val message: String? = null
)

data class ProjectEditForm(
    val isVisible: Boolean = false,
    val projectId: String? = null,
    val title: String = "",
    val description: String = "",
    val status: ProjectStatus = ProjectStatus.BACKLOG,
    val investmentText: String = "",
    val profitText: String = "",
    val productivityText: String = "",
    val deadlineDaysText: String = ""
)
