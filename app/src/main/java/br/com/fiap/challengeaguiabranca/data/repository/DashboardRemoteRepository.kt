@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package br.com.fiap.challengeaguiabranca.data.repository

import br.com.fiap.challengeaguiabranca.data.remote.ApiCaller
import br.com.fiap.challengeaguiabranca.data.remote.RefreshBus
import br.com.fiap.challengeaguiabranca.data.remote.api.InnovationApi
import br.com.fiap.challengeaguiabranca.data.remote.mapper.toDomain
import br.com.fiap.challengeaguiabranca.domain.model.LeaderDashboard
import br.com.fiap.challengeaguiabranca.domain.model.ManagerDashboard
import br.com.fiap.challengeaguiabranca.domain.model.OperatorActivity
import br.com.fiap.challengeaguiabranca.domain.model.RoiDashboardSummary
import br.com.fiap.challengeaguiabranca.domain.model.StrategyReturn
import br.com.fiap.challengeaguiabranca.domain.repository.DashboardRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.util.logging.Logger

class DashboardRemoteRepository(
    private val api: InnovationApi,
    private val apiCaller: ApiCaller,
    private val refreshBus: RefreshBus = RefreshBus()
) : DashboardRepository {

    override fun observeManager(): Flow<ManagerDashboard> =
        refreshBus.updates()
            .flatMapLatest {
                flow {
                    emit(apiCaller.execute { api.managerDashboard() }.toDomain())
                }.catch { error ->
                    Logger.getLogger("DashboardRemote").warning("manager failed: ${error.message}")
                    emit(
                        ManagerDashboard(
                            pendingIdeasCount = 0,
                            activeProjectsCount = 0,
                            ideasReceivedThisMonth = 0,
                            ideasReceivedTrendLabel = "",
                            approvalRatePercent = 0,
                            approvalRateTrendLabel = "",
                            monthlyBars = emptyList()
                        )
                    )
                }
            }
            .flowOn(Dispatchers.IO)

    override fun observeLeader(): Flow<LeaderDashboard> =
        refreshBus.updates()
            .flatMapLatest {
                flow {
                    emit(apiCaller.execute { api.leaderDashboard() }.toDomain())
                }.catch { error ->
                    Logger.getLogger("DashboardRemote").warning("leader failed: ${error.message}")
                    emit(
                        LeaderDashboard(
                            summary = RoiDashboardSummary(
                                totalInvestment = 0.0,
                                totalObtainedProfit = 0.0,
                                overallRoiPercent = 0.0,
                                averageProductivityGainPercent = 0.0,
                                activeProjectsCount = 0,
                                completedProjectsCount = 0
                            ),
                            statusChartLabels = emptyList(),
                            statusChartValues = emptyList()
                        )
                    )
                }
            }
            .flowOn(Dispatchers.IO)

    override fun observeStrategyReturns(): Flow<List<StrategyReturn>> =
        refreshBus.updates()
            .flatMapLatest {
                flow {
                    val response = apiCaller.execute { api.strategyReturns() }
                    emit(response.items.map { it.toDomain() })
                }.catch { error ->
                    Logger.getLogger("DashboardRemote").warning("strategies failed: ${error.message}")
                    emit(emptyList())
                }
            }
            .flowOn(Dispatchers.IO)

    override fun observeRankings(period: String): Flow<List<OperatorActivity>> =
        refreshBus.updates()
            .flatMapLatest {
                flow {
                    val response = apiCaller.execute { api.rankings(period) }
                    emit(response.items.map { it.toDomain() })
                }.catch { error ->
                    Logger.getLogger("DashboardRemote").warning("rankings failed: ${error.message}")
                    emit(emptyList())
                }
            }
            .flowOn(Dispatchers.IO)

    override suspend fun leaderSummary(): RoiDashboardSummary =
        apiCaller.execute { api.leaderDashboard() }.toDomain().summary
}
