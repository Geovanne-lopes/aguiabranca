package br.com.fiap.challengeaguiabranca.ui.feature.leader.home

import androidx.compose.runtime.Composable
import br.com.fiap.challengeaguiabranca.ui.feature.leader.LeaderMainScreen

@Composable
fun LeaderHomeScreen(onLogout: () -> Unit) {
    LeaderMainScreen(onLogout = onLogout)
}
