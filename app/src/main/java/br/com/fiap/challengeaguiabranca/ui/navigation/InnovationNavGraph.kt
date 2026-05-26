package br.com.fiap.challengeaguiabranca.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import br.com.fiap.challengeaguiabranca.ui.feature.auth.login.LoginScreen
import br.com.fiap.challengeaguiabranca.ui.feature.leader.home.LeaderHomeScreen
import br.com.fiap.challengeaguiabranca.ui.feature.manager.home.ManagerHomeScreen
import br.com.fiap.challengeaguiabranca.ui.feature.operator.home.OperatorHomeScreen

@Composable
fun InnovationNavGraph(
    navController: NavHostController,
    startDestination: String,
    modifier: Modifier = Modifier,
    onToggleTheme: () -> Unit = {}
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(Routes.LOGIN) {
            LoginScreen(
                onNavigateToHome = { route ->
                    navController.navigate(route) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onToggleTheme = onToggleTheme
            )
        }

        composable(Routes.OPERATOR_HOME) {
            OperatorHomeScreen(
                onLogout = { navigateToLogin(navController) }
            )
        }

        composable(Routes.MANAGER_HOME) {
            ManagerHomeScreen(
                onLogout = { navigateToLogin(navController) }
            )
        }

        composable(Routes.LEADER_HOME) {
            LeaderHomeScreen(
                onLogout = { navigateToLogin(navController) }
            )
        }
    }
}

private fun navigateToLogin(navController: NavHostController) {
    val startId = navController.graph.startDestinationId
    navController.navigate(Routes.LOGIN) {
        popUpTo(startId) { inclusive = true }
        launchSingleTop = true
    }
}
