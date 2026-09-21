package br.com.fiap.challengeaguiabranca.domain.usecase.dashboard

import br.com.fiap.challengeaguiabranca.domain.model.LeaderDashboard
import br.com.fiap.challengeaguiabranca.domain.model.ManagerDashboard
import br.com.fiap.challengeaguiabranca.domain.model.RoiDashboardSummary
import br.com.fiap.challengeaguiabranca.domain.model.StrategyReturn
import br.com.fiap.challengeaguiabranca.domain.repository.DashboardRepository
import kotlinx.coroutines.flow.Flow

class GetRoiDashboardSummaryUseCase(
    private val dashboardRepository: DashboardRepository
) {
    suspend operator fun invoke(): RoiDashboardSummary = dashboardRepository.leaderSummary()
}

class ObserveManagerDashboardUseCase(
    private val dashboardRepository: DashboardRepository
) {
    operator fun invoke(): Flow<ManagerDashboard> = dashboardRepository.observeManager()
}

class ObserveLeaderDashboardUseCase(
    private val dashboardRepository: DashboardRepository
) {
    operator fun invoke(): Flow<LeaderDashboard> = dashboardRepository.observeLeader()
}

class ObserveStrategyReturnsUseCase(
    private val dashboardRepository: DashboardRepository
) {
    operator fun invoke(): Flow<List<StrategyReturn>> = dashboardRepository.observeStrategyReturns()
}
