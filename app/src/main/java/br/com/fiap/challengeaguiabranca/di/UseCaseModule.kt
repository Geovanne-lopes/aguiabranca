package br.com.fiap.challengeaguiabranca.di

import br.com.fiap.challengeaguiabranca.domain.usecase.auth.AuthenticateUserUseCase
import br.com.fiap.challengeaguiabranca.domain.usecase.insight.FetchDailyInsightUseCase
import br.com.fiap.challengeaguiabranca.domain.usecase.auth.FetchSeedUsersUseCase
import br.com.fiap.challengeaguiabranca.domain.usecase.dashboard.GetRoiDashboardSummaryUseCase
import br.com.fiap.challengeaguiabranca.domain.usecase.guideline.CreateGuidelineUseCase
import br.com.fiap.challengeaguiabranca.domain.usecase.guideline.DeleteGuidelineUseCase
import br.com.fiap.challengeaguiabranca.domain.usecase.guideline.ObserveGuidelinesUseCase
import br.com.fiap.challengeaguiabranca.domain.usecase.guideline.UpdateGuidelineUseCase
import br.com.fiap.challengeaguiabranca.domain.usecase.idea.GetPendingIdeasCountUseCase
import br.com.fiap.challengeaguiabranca.domain.usecase.idea.ObserveAllIdeasUseCase
import br.com.fiap.challengeaguiabranca.domain.usecase.idea.ObserveIdeasByAuthorUseCase
import br.com.fiap.challengeaguiabranca.domain.usecase.idea.SubmitIdeaUseCase
import br.com.fiap.challengeaguiabranca.domain.usecase.idea.UpdateIdeaStatusUseCase
import br.com.fiap.challengeaguiabranca.domain.usecase.project.CreateProjectFromIdeaUseCase
import br.com.fiap.challengeaguiabranca.domain.usecase.project.DeleteProjectUseCase
import br.com.fiap.challengeaguiabranca.domain.usecase.project.GetActiveProjectsCountUseCase
import br.com.fiap.challengeaguiabranca.domain.usecase.project.ObserveAllProjectsUseCase
import br.com.fiap.challengeaguiabranca.domain.usecase.project.UpdateProjectUseCase
import br.com.fiap.challengeaguiabranca.domain.usecase.session.ClearSessionUseCase
import br.com.fiap.challengeaguiabranca.domain.usecase.session.GetCurrentUserUseCase
import br.com.fiap.challengeaguiabranca.domain.usecase.session.IsLoggedInUseCase
import br.com.fiap.challengeaguiabranca.domain.usecase.session.ObserveCurrentUserUseCase
import br.com.fiap.challengeaguiabranca.domain.usecase.session.SaveUserSessionUseCase
import org.koin.dsl.module

val useCaseModule = module {

    // Auth
    factory { FetchSeedUsersUseCase(get()) }
    factory { FetchDailyInsightUseCase(get()) }
    factory { AuthenticateUserUseCase(get(), get()) }

    // Session
    factory { SaveUserSessionUseCase(get()) }
    factory { GetCurrentUserUseCase(get()) }
    factory { ObserveCurrentUserUseCase(get()) }
    factory { ClearSessionUseCase(get()) }
    factory { IsLoggedInUseCase(get()) }

    // Ideas
    factory { SubmitIdeaUseCase(get()) }
    factory { ObserveIdeasByAuthorUseCase(get()) }
    factory { ObserveAllIdeasUseCase(get()) }
    factory { UpdateIdeaStatusUseCase(get()) }
    factory { GetPendingIdeasCountUseCase(get()) }

    // Projects
    factory { ObserveAllProjectsUseCase(get()) }
    factory { CreateProjectFromIdeaUseCase(get(), get()) }
    factory { UpdateProjectUseCase(get()) }
    factory { DeleteProjectUseCase(get()) }
    factory { GetActiveProjectsCountUseCase(get()) }

    // Guidelines
    factory { ObserveGuidelinesUseCase(get()) }
    factory { CreateGuidelineUseCase(get()) }
    factory { UpdateGuidelineUseCase(get()) }
    factory { DeleteGuidelineUseCase(get()) }

    // Dashboard
    factory { GetRoiDashboardSummaryUseCase(get()) }
}
