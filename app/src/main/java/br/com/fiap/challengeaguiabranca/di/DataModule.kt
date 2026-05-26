package br.com.fiap.challengeaguiabranca.di

import br.com.fiap.challengeaguiabranca.data.local.datastore.ManagerSuggestionsDataStore
import br.com.fiap.challengeaguiabranca.data.repository.GuidelineRepositoryImpl
import br.com.fiap.challengeaguiabranca.data.repository.ManagerSuggestionRepositoryImpl
import br.com.fiap.challengeaguiabranca.data.repository.IdeaRepositoryImpl
import br.com.fiap.challengeaguiabranca.data.repository.InsightRepositoryImpl
import br.com.fiap.challengeaguiabranca.data.repository.ProjectRepositoryImpl
import br.com.fiap.challengeaguiabranca.data.repository.SessionRepositoryImpl
import br.com.fiap.challengeaguiabranca.data.repository.UserRepositoryImpl
import br.com.fiap.challengeaguiabranca.domain.repository.GuidelineRepository
import br.com.fiap.challengeaguiabranca.domain.repository.ManagerSuggestionRepository
import br.com.fiap.challengeaguiabranca.domain.repository.IdeaRepository
import br.com.fiap.challengeaguiabranca.domain.repository.InsightRepository
import br.com.fiap.challengeaguiabranca.domain.repository.ProjectRepository
import br.com.fiap.challengeaguiabranca.domain.repository.SessionRepository
import br.com.fiap.challengeaguiabranca.domain.repository.UserRepository
import org.koin.dsl.module

val dataModule = module {

    // Remote
    single<UserRepository> { UserRepositoryImpl(get()) }
    single<InsightRepository> { InsightRepositoryImpl(get()) }

    // Local (Room)
    single<IdeaRepository> { IdeaRepositoryImpl(get()) }
    single<ProjectRepository> { ProjectRepositoryImpl(get()) }
    single<GuidelineRepository> { GuidelineRepositoryImpl(get()) }
    single<SessionRepository> { SessionRepositoryImpl(get()) }
    single { ManagerSuggestionsDataStore(get()) }
    single<ManagerSuggestionRepository> { ManagerSuggestionRepositoryImpl(get()) }
}
