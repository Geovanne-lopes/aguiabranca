package br.com.fiap.challengeaguiabranca.di

import br.com.fiap.challengeaguiabranca.data.repository.AiInsightRemoteRepository
import br.com.fiap.challengeaguiabranca.data.repository.AuthRemoteRepository
import br.com.fiap.challengeaguiabranca.data.repository.DashboardRemoteRepository
import br.com.fiap.challengeaguiabranca.data.repository.GuidelineRemoteRepository
import br.com.fiap.challengeaguiabranca.data.repository.IdeaRemoteRepository
import br.com.fiap.challengeaguiabranca.data.repository.InsightRemoteRepository
import br.com.fiap.challengeaguiabranca.data.repository.NotificationRemoteRepository
import br.com.fiap.challengeaguiabranca.data.repository.ProjectRemoteRepository
import br.com.fiap.challengeaguiabranca.data.repository.SessionRepositoryImpl
import br.com.fiap.challengeaguiabranca.data.repository.SuggestionRemoteRepository
import br.com.fiap.challengeaguiabranca.data.repository.UserRemoteRepository
import br.com.fiap.challengeaguiabranca.domain.repository.AiInsightRepository
import br.com.fiap.challengeaguiabranca.domain.repository.AuthRepository
import br.com.fiap.challengeaguiabranca.domain.repository.DashboardRepository
import br.com.fiap.challengeaguiabranca.domain.repository.GuidelineRepository
import br.com.fiap.challengeaguiabranca.domain.repository.IdeaRepository
import br.com.fiap.challengeaguiabranca.domain.repository.InsightRepository
import br.com.fiap.challengeaguiabranca.domain.repository.ManagerSuggestionRepository
import br.com.fiap.challengeaguiabranca.domain.repository.NotificationRepository
import br.com.fiap.challengeaguiabranca.domain.repository.ProjectRepository
import br.com.fiap.challengeaguiabranca.domain.repository.SessionRepository
import br.com.fiap.challengeaguiabranca.domain.repository.UserRepository
import org.koin.core.qualifier.named
import org.koin.dsl.module

val dataModule = module {
    single<AuthRepository> {
        AuthRemoteRepository(
            publicApi = get(named(PUBLIC_API)),
            securedApi = get(named(SECURED_API)),
            apiCaller = get(),
            tokenStore = get()
        )
    }
    single<UserRepository> { UserRemoteRepository(get(named(SECURED_API)), get()) }
    single<InsightRepository> { InsightRemoteRepository(get(named(SECURED_API)), get()) }
    single<IdeaRepository> { IdeaRemoteRepository(get(named(SECURED_API)), get()) }
    single<ProjectRepository> { ProjectRemoteRepository(get(named(SECURED_API)), get()) }
    single<GuidelineRepository> { GuidelineRemoteRepository(get(named(SECURED_API)), get()) }
    single<ManagerSuggestionRepository> { SuggestionRemoteRepository(get(named(SECURED_API)), get()) }
    single<DashboardRepository> { DashboardRemoteRepository(get(named(SECURED_API)), get()) }
    single<NotificationRepository> { NotificationRemoteRepository(get(named(SECURED_API)), get()) }
    single<AiInsightRepository> { AiInsightRemoteRepository(get(named(SECURED_API)), get()) }
    single<SessionRepository> { SessionRepositoryImpl(get()) }
}
