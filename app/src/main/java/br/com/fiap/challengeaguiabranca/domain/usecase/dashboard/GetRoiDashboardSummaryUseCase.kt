package br.com.fiap.challengeaguiabranca.domain.usecase.dashboard

import br.com.fiap.challengeaguiabranca.domain.model.RoiDashboardSummary
import br.com.fiap.challengeaguiabranca.domain.repository.ProjectRepository
import kotlinx.coroutines.flow.first

class GetRoiDashboardSummaryUseCase(
    private val projectRepository: ProjectRepository
) {
    suspend operator fun invoke(): RoiDashboardSummary {
        val projects = projectRepository.observeAll().first()
        return RoiDashboardSummary.fromProjects(projects)
    }
}
