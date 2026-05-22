package br.com.fiap.challengeaguiabranca.ui.feature.leader

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import br.com.fiap.challengeaguiabranca.R
import br.com.fiap.challengeaguiabranca.ui.feature.leader.components.LeaderBottomBar
import br.com.fiap.challengeaguiabranca.ui.feature.leader.guidelines.LeaderGuidelinesScreen
import br.com.fiap.challengeaguiabranca.ui.feature.leader.home.LeaderDashboardScreen
import br.com.fiap.challengeaguiabranca.ui.feature.leader.tracking.LeaderTrackingScreen
import br.com.fiap.challengeaguiabranca.ui.feature.shared.RoleHomeViewModel
import br.com.fiap.challengeaguiabranca.ui.theme.InnovateBackground
import br.com.fiap.challengeaguiabranca.ui.theme.InnovateTextPrimary
import org.koin.androidx.compose.koinViewModel

@Composable
fun LeaderMainScreen(
    onLogout: () -> Unit,
    sessionViewModel: RoleHomeViewModel = koinViewModel()
) {
    var selectedTab by remember { mutableStateOf(LeaderTab.HOME) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = InnovateBackground,
        topBar = {
            LeaderTopBar(
                title = topBarTitle(selectedTab),
                onLogout = { sessionViewModel.logout(onLogout) }
            )
        },
        bottomBar = {
            LeaderBottomBar(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )
        }
    ) { padding ->
        when (selectedTab) {
            LeaderTab.HOME -> LeaderDashboardScreen(modifier = Modifier.padding(padding))
            LeaderTab.GUIDELINES -> LeaderGuidelinesScreen(modifier = Modifier.padding(padding))
            LeaderTab.TRACKING -> LeaderTrackingScreen(modifier = Modifier.padding(padding))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LeaderTopBar(title: String, onLogout: () -> Unit) {
    TopAppBar(
        title = { Text(title, fontWeight = FontWeight.Bold) },
        navigationIcon = {
            IconButton(onClick = { }) {
                Icon(Icons.Default.Menu, contentDescription = null)
            }
        },
        actions = {
            IconButton(onClick = { }) {
                Icon(Icons.Default.Notifications, contentDescription = null)
            }
            IconButton(onClick = onLogout) {
                Icon(Icons.Default.Logout, contentDescription = stringResource(R.string.home_logout))
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = InnovateBackground,
            titleContentColor = InnovateTextPrimary
        )
    )
}

@Composable
private fun topBarTitle(tab: LeaderTab): String = when (tab) {
    LeaderTab.HOME -> stringResource(R.string.leader_nav_home)
    LeaderTab.GUIDELINES -> stringResource(R.string.leader_nav_guidelines)
    LeaderTab.TRACKING -> stringResource(R.string.leader_nav_tracking)
}
