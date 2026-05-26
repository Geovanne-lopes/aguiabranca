package br.com.fiap.challengeaguiabranca.di

import android.util.Log
import br.com.fiap.challengeaguiabranca.data.local.DatabaseSeeder
import br.com.fiap.challengeaguiabranca.domain.repository.GuidelineRepository
import br.com.fiap.challengeaguiabranca.domain.repository.IdeaRepository
import br.com.fiap.challengeaguiabranca.domain.repository.ProjectRepository
import br.com.fiap.challengeaguiabranca.domain.repository.SessionRepository
import br.com.fiap.challengeaguiabranca.domain.usecase.dashboard.GetRoiDashboardSummaryUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * Smoke test Room em DEBUG — Logcat tag: LocalBootstrap
 */
object LocalBootstrapVerifier {

    private const val TAG = "LocalBootstrap"
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    fun verify(
        databaseSeeder: DatabaseSeeder,
        ideaRepository: IdeaRepository,
        projectRepository: ProjectRepository,
        guidelineRepository: GuidelineRepository,
        sessionRepository: SessionRepository,
        getRoiDashboardSummaryUseCase: GetRoiDashboardSummaryUseCase
    ) {
        scope.launch {
            runCatching {
                databaseSeeder.seedIfEmpty()
                databaseSeeder.seedMockIdeasIfEmpty()
                val ideas = ideaRepository.observeAll().first()
                val projects = projectRepository.observeAll().first()
                val guidelines = guidelineRepository.observeAll().first()
                val loggedIn = sessionRepository.isLoggedIn()
                val roi = getRoiDashboardSummaryUseCase()

                Log.d(TAG, "Room OK — ideias=${ideas.size}, projetos=${projects.size}, diretrizes=${guidelines.size}")
                Log.d(TAG, "Sessão ativa=$loggedIn")
                Log.d(TAG, "ROI agregado: investimento=${roi.totalInvestment}, lucro=${roi.totalObtainedProfit}, roi%=${roi.overallRoiPercent}")
                guidelines.forEach { g ->
                    Log.d(TAG, "  Diretriz: ${g.title}")
                }
            }.onFailure { error ->
                Log.e(TAG, "Falha na verificação local", error)
            }
        }
    }
}
