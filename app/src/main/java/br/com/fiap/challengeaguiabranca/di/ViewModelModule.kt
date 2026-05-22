package br.com.fiap.challengeaguiabranca.di

import br.com.fiap.challengeaguiabranca.ui.feature.auth.login.LoginViewModel
import br.com.fiap.challengeaguiabranca.ui.feature.leader.guidelines.LeaderGuidelinesViewModel
import br.com.fiap.challengeaguiabranca.ui.feature.leader.home.LeaderDashboardViewModel
import br.com.fiap.challengeaguiabranca.ui.feature.leader.tracking.LeaderTrackingViewModel
import br.com.fiap.challengeaguiabranca.ui.feature.manager.profile.ManagerProfileViewModel
import br.com.fiap.challengeaguiabranca.ui.feature.manager.curation.ManagerCurationViewModel
import br.com.fiap.challengeaguiabranca.ui.feature.manager.guidelines.ManagerGuidelinesViewModel
import br.com.fiap.challengeaguiabranca.ui.feature.manager.home.ManagerDashboardViewModel
import br.com.fiap.challengeaguiabranca.ui.feature.manager.projects.ManagerProjectsViewModel
import br.com.fiap.challengeaguiabranca.ui.feature.operator.home.OperatorHomeViewModel
import br.com.fiap.challengeaguiabranca.ui.feature.operator.ideas.OperatorIdeasViewModel
import br.com.fiap.challengeaguiabranca.ui.feature.shared.RoleHomeViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { LoginViewModel(get()) }
    viewModel { RoleHomeViewModel(get(), get()) }
    viewModel { OperatorHomeViewModel(get(), get(), get(), get(), get()) }
    viewModel { OperatorIdeasViewModel(get(), get(), get()) }
    viewModel { ManagerDashboardViewModel(get(), get(), get()) }
    viewModel { ManagerCurationViewModel(get(), get()) }
    viewModel { ManagerGuidelinesViewModel(get()) }
    viewModel { ManagerProjectsViewModel(get(), get(), get(), get(), get(), get()) }
    viewModel { ManagerProfileViewModel(get(), get(), get()) }
    viewModel { LeaderDashboardViewModel(get(), get()) }
    viewModel { LeaderGuidelinesViewModel(get(), get(), get(), get(), get()) }
    viewModel { LeaderTrackingViewModel(get()) }
}
