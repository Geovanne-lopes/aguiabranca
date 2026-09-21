package br.com.fiap.challengeaguiabranca.di

import br.com.fiap.challengeaguiabranca.domain.usecase.auth.AuthenticateUserUseCase
import br.com.fiap.challengeaguiabranca.domain.usecase.auth.RegisterAccountUseCase
import br.com.fiap.challengeaguiabranca.domain.usecase.auth.ResetPasswordUseCase
import br.com.fiap.challengeaguiabranca.domain.usecase.auth.RestoreSessionUseCase
import br.com.fiap.challengeaguiabranca.domain.usecase.insight.FetchDailyInsightUseCase
import br.com.fiap.challengeaguiabranca.domain.usecase.auth.FetchSeedUsersUseCase
import br.com.fiap.challengeaguiabranca.domain.usecase.ai.GenerateAiInsightUseCase
import br.com.fiap.challengeaguiabranca.domain.usecase.ai.GetLatestAiInsightUseCase
import br.com.fiap.challengeaguiabranca.domain.usecase.dashboard.GetRoiDashboardSummaryUseCase
import br.com.fiap.challengeaguiabranca.domain.usecase.dashboard.ObserveLeaderDashboardUseCase
import br.com.fiap.challengeaguiabranca.domain.usecase.dashboard.ObserveStrategyReturnsUseCase
import br.com.fiap.challengeaguiabranca.domain.usecase.dashboard.ObserveManagerDashboardUseCase
import br.com.fiap.challengeaguiabranca.domain.usecase.notification.ObserveManagementNotificationsUseCase
import br.com.fiap.challengeaguiabranca.domain.usecase.notification.ObserveOperatorNotificationsUseCase
import br.com.fiap.challengeaguiabranca.domain.usecase.user.ListOperatorsUseCase
import br.com.fiap.challengeaguiabranca.domain.usecase.guideline.CreateGuidelineUseCase
import br.com.fiap.challengeaguiabranca.domain.usecase.guideline.DeleteGuidelineUseCase
import br.com.fiap.challengeaguiabranca.domain.usecase.guideline.GetGuidelineHistoryUseCase
import br.com.fiap.challengeaguiabranca.domain.usecase.guideline.ObserveGuidelinesUseCase
import br.com.fiap.challengeaguiabranca.domain.usecase.guideline.UpdateGuidelineUseCase
import br.com.fiap.challengeaguiabranca.domain.usecase.idea.GetPendingIdeasCountUseCase
import br.com.fiap.challengeaguiabranca.domain.usecase.idea.ObserveAllIdeasUseCase
import br.com.fiap.challengeaguiabranca.domain.usecase.idea.ObserveIdeasByAuthorUseCase
import br.com.fiap.challengeaguiabranca.domain.usecase.idea.DeleteIdeaUseCase
import br.com.fiap.challengeaguiabranca.domain.usecase.idea.SubmitIdeaUseCase
import br.com.fiap.challengeaguiabranca.domain.usecase.idea.UpdateIdeaUseCase
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
import br.com.fiap.challengeaguiabranca.domain.usecase.manager.GetMonthlyOperatorRankingUseCase
import br.com.fiap.challengeaguiabranca.domain.usecase.manager.GetOperatorActivityRankingUseCase
import br.com.fiap.challengeaguiabranca.domain.usecase.manager.ObserveManagerSuggestionsUseCase
import br.com.fiap.challengeaguiabranca.domain.usecase.manager.ObserveSuggestionsForUserUseCase
import br.com.fiap.challengeaguiabranca.domain.usecase.manager.SendManagerSuggestionUseCase
import br.com.fiap.challengeaguiabranca.domain.usecase.session.UpdateUserProfileUseCase
import org.koin.dsl.module

val useCaseModule = module {

    // Auth
    factory { FetchSeedUsersUseCase(get()) }
    factory { FetchDailyInsightUseCase(get()) }
    factory { AuthenticateUserUseCase(get(), get()) }
    factory { RegisterAccountUseCase(get()) }
    factory { ResetPasswordUseCase(get()) }
    factory { RestoreSessionUseCase(get(), get()) }
    factory { ListOperatorsUseCase(get()) }
    factory { GetLatestAiInsightUseCase(get()) }
    factory { GenerateAiInsightUseCase(get()) }
    factory { ObserveOperatorNotificationsUseCase(get()) }
    factory { ObserveManagementNotificationsUseCase(get()) }
    factory { ObserveManagerDashboardUseCase(get()) }
    factory { ObserveLeaderDashboardUseCase(get()) }
    factory { ObserveStrategyReturnsUseCase(get()) }

    // Session
    factory { SaveUserSessionUseCase(get()) }
    factory { GetCurrentUserUseCase(get()) }
    factory { ObserveCurrentUserUseCase(get()) }
    factory { ClearSessionUseCase(get(), get()) }
    factory { IsLoggedInUseCase(get()) }
    factory { UpdateUserProfileUseCase(get(), get()) }

    // Ideas
    factory { SubmitIdeaUseCase(get()) }
    factory { UpdateIdeaUseCase(get()) }
    factory { DeleteIdeaUseCase(get()) }
    factory { ObserveIdeasByAuthorUseCase(get()) }
    factory { ObserveAllIdeasUseCase(get()) }
    factory { UpdateIdeaStatusUseCase(get()) }
    factory { GetPendingIdeasCountUseCase(get()) }

    // Projects
    factory { ObserveAllProjectsUseCase(get()) }
    factory { CreateProjectFromIdeaUseCase(get()) }
    factory { UpdateProjectUseCase(get()) }
    factory { DeleteProjectUseCase(get()) }
    factory { GetActiveProjectsCountUseCase(get()) }

    // Guidelines
    factory { ObserveGuidelinesUseCase(get()) }
    factory { CreateGuidelineUseCase(get()) }
    factory { UpdateGuidelineUseCase(get()) }
    factory { DeleteGuidelineUseCase(get()) }
    factory { GetGuidelineHistoryUseCase(get()) }

    // Dashboard
    factory { GetRoiDashboardSummaryUseCase(get()) }

    // Manager
    factory { GetOperatorActivityRankingUseCase(get()) }
    factory { GetMonthlyOperatorRankingUseCase(get()) }
    factory { SendManagerSuggestionUseCase(get()) }
    factory { ObserveManagerSuggestionsUseCase(get()) }
    factory { ObserveSuggestionsForUserUseCase(get()) }
}
