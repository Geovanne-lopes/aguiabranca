package br.com.fiap.challengeaguiabranca.domain.usecase.project

import br.com.fiap.challengeaguiabranca.domain.model.Idea
import br.com.fiap.challengeaguiabranca.domain.model.IdeaStatus
import br.com.fiap.challengeaguiabranca.domain.model.Project
import br.com.fiap.challengeaguiabranca.domain.model.ProjectStatus
import br.com.fiap.challengeaguiabranca.domain.repository.IdeaRepository
import br.com.fiap.challengeaguiabranca.domain.repository.ProjectRepository
import br.com.fiap.challengeaguiabranca.domain.util.IdGenerator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class ObserveAllProjectsUseCase(
    private val projectRepository: ProjectRepository
) {
    operator fun invoke(): Flow<List<Project>> = projectRepository.observeAll()
}

class CreateProjectFromIdeaUseCase(
    private val ideaRepository: IdeaRepository,
    private val projectRepository: ProjectRepository
) {
    suspend operator fun invoke(idea: Idea, managerId: String): Project {
        require(
            idea.status == IdeaStatus.APPROVED || idea.status == IdeaStatus.PRIORITIZED
        ) {
            "Somente ideias aprovadas ou priorizadas podem virar projeto."
        }
        val existing = projectRepository.getByIdeaId(idea.id)
        if (existing != null) return existing

        val now = System.currentTimeMillis()
        val project = Project(
            id = IdGenerator.newId(),
            ideaId = idea.id,
            title = idea.title,
            description = idea.description,
            managerId = managerId,
            createdAtEpochMillis = now,
            updatedAtEpochMillis = now
        )
        projectRepository.insert(project)
        return project
    }
}

class UpdateProjectUseCase(
    private val projectRepository: ProjectRepository
) {
    suspend operator fun invoke(project: Project) {
        projectRepository.update(
            project.copy(updatedAtEpochMillis = System.currentTimeMillis())
        )
    }
}

class DeleteProjectUseCase(
    private val projectRepository: ProjectRepository
) {
    suspend operator fun invoke(projectId: String) {
        projectRepository.delete(projectId)
    }
}

class GetActiveProjectsCountUseCase(
    private val projectRepository: ProjectRepository
) {
    suspend operator fun invoke(): Int {
        val projects = projectRepository.observeAll().first()
        return projects.count { it.status != ProjectStatus.COMPLETED }
    }
}
