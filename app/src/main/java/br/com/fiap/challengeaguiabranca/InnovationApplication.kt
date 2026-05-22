package br.com.fiap.challengeaguiabranca

import android.app.Application
import br.com.fiap.challengeaguiabranca.data.local.DatabaseSeeder
import br.com.fiap.challengeaguiabranca.di.LocalBootstrapVerifier
import br.com.fiap.challengeaguiabranca.di.RemoteBootstrapVerifier
import br.com.fiap.challengeaguiabranca.di.appModules
import br.com.fiap.challengeaguiabranca.domain.repository.GuidelineRepository
import br.com.fiap.challengeaguiabranca.domain.repository.IdeaRepository
import br.com.fiap.challengeaguiabranca.domain.repository.InsightRepository
import br.com.fiap.challengeaguiabranca.domain.repository.ProjectRepository
import br.com.fiap.challengeaguiabranca.domain.repository.SessionRepository
import br.com.fiap.challengeaguiabranca.domain.repository.UserRepository
import br.com.fiap.challengeaguiabranca.domain.usecase.dashboard.GetRoiDashboardSummaryUseCase
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.java.KoinJavaComponent.inject

class InnovationApplication : Application() {

    private val userRepository: UserRepository by inject(UserRepository::class.java)
    private val insightRepository: InsightRepository by inject(InsightRepository::class.java)
    private val databaseSeeder: DatabaseSeeder by inject(DatabaseSeeder::class.java)
    private val ideaRepository: IdeaRepository by inject(IdeaRepository::class.java)
    private val projectRepository: ProjectRepository by inject(ProjectRepository::class.java)
    private val guidelineRepository: GuidelineRepository by inject(GuidelineRepository::class.java)
    private val sessionRepository: SessionRepository by inject(SessionRepository::class.java)
    private val getRoiDashboardSummaryUseCase: GetRoiDashboardSummaryUseCase by inject(
        GetRoiDashboardSummaryUseCase::class.java
    )

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger(Level.ERROR)
            androidContext(this@InnovationApplication)
            modules(appModules)
        }
        if (BuildConfig.DEBUG) {
            RemoteBootstrapVerifier.verify(userRepository, insightRepository)
            LocalBootstrapVerifier.verify(
                databaseSeeder = databaseSeeder,
                ideaRepository = ideaRepository,
                projectRepository = projectRepository,
                guidelineRepository = guidelineRepository,
                sessionRepository = sessionRepository,
                getRoiDashboardSummaryUseCase = getRoiDashboardSummaryUseCase
            )
        }
    }
}
