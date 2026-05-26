package br.com.fiap.challengeaguiabranca.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import br.com.fiap.challengeaguiabranca.domain.model.UserRole
import br.com.fiap.challengeaguiabranca.domain.usecase.session.GetCurrentUserUseCase
import br.com.fiap.challengeaguiabranca.domain.usecase.session.IsLoggedInUseCase
import br.com.fiap.challengeaguiabranca.ui.navigation.InnovationNavGraph
import br.com.fiap.challengeaguiabranca.ui.navigation.Routes
import br.com.fiap.challengeaguiabranca.ui.theme.InnovatePrimary
import org.koin.compose.koinInject

@Composable
fun InnovationApp(
    modifier: Modifier = Modifier,
    darkTheme: Boolean = false,
    onToggleTheme: () -> Unit = {},
    isLoggedInUseCase: IsLoggedInUseCase = koinInject(),
    getCurrentUserUseCase: GetCurrentUserUseCase = koinInject()
) {
    val navController = rememberNavController()
    var startDestination by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        startDestination = resolveStartDestination(
            isLoggedIn = isLoggedInUseCase(),
            role = getCurrentUserUseCase()?.role
        )
    }

    when (val destination = startDestination) {
        null -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = InnovatePrimary)
            }
        }
        else -> {
            InnovationNavGraph(
                navController = navController,
                startDestination = destination,
                modifier = modifier,
                onToggleTheme = onToggleTheme
            )
        }
    }
}

private fun resolveStartDestination(isLoggedIn: Boolean, role: UserRole?): String {
    if (!isLoggedIn || role == null) return Routes.LOGIN
    return Routes.homeForRole(role)
}
