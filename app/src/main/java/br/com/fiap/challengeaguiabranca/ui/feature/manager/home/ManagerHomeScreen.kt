package br.com.fiap.challengeaguiabranca.ui.feature.manager.home

import androidx.compose.runtime.Composable
import br.com.fiap.challengeaguiabranca.ui.feature.manager.ManagerMainScreen

@Composable
fun ManagerHomeScreen(onLogout: () -> Unit) {
    ManagerMainScreen(onLogout = onLogout)
}
