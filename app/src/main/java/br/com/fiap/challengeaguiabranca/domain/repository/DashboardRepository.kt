package br.com.fiap.challengeaguiabranca.domain.repository

import br.com.fiap.challengeaguiabranca.domain.model.LeaderDashboard
import br.com.fiap.challengeaguiabranca.domain.model.ManagerDashboard
import br.com.fiap.challengeaguiabranca.domain.model.OperatorActivity
import br.com.fiap.challengeaguiabranca.domain.model.RoiDashboardSummary
import br.com.fiap.challengeaguiabranca.domain.model.StrategyReturn
import kotlinx.coroutines.flow.Flow

interface DashboardRepository {
    fun observeManager(): Flow<ManagerDashboard>
    fun observeLeader(): Flow<LeaderDashboard>
    fun observeStrategyReturns(): Flow<List<StrategyReturn>>
    fun observeRankings(period: String): Flow<List<OperatorActivity>>
    suspend fun leaderSummary(): RoiDashboardSummary
}
