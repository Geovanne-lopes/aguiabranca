package br.com.fiap.challengeaguiabranca.ui.feature.manager

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import br.com.fiap.challengeaguiabranca.ui.feature.manager.components.ManagerBottomBar
import br.com.fiap.challengeaguiabranca.ui.feature.manager.curation.ManagerCurationScreen
import br.com.fiap.challengeaguiabranca.ui.feature.manager.guidelines.ManagerGuidelinesScreen
import br.com.fiap.challengeaguiabranca.ui.feature.manager.home.ManagerDashboardScreen
import br.com.fiap.challengeaguiabranca.ui.feature.manager.profile.ManagerProfileScreen
import br.com.fiap.challengeaguiabranca.ui.feature.manager.projects.ManagerProjectsScreen
import br.com.fiap.challengeaguiabranca.ui.feature.shared.RoleHomeViewModel
import br.com.fiap.challengeaguiabranca.ui.theme.InnovateBackground
import br.com.fiap.challengeaguiabranca.ui.theme.InnovateTextPrimary
import org.koin.androidx.compose.koinViewModel

@Composable
fun ManagerMainScreen(
    onLogout: () -> Unit,
    sessionViewModel: RoleHomeViewModel = koinViewModel()
) {
    var selectedTab by remember { mutableStateOf(ManagerTab.HOME) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = InnovateBackground,
        topBar = {
            ManagerTopBar(
                title = topBarTitle(selectedTab),
                onLogout = { sessionViewModel.logout(onLogout) }
            )
        },
        bottomBar = {
            ManagerBottomBar(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )
        }
    ) { padding ->
        when (selectedTab) {
            ManagerTab.HOME -> ManagerDashboardScreen(
                modifier = Modifier.padding(padding),
                onOpenCuration = { selectedTab = ManagerTab.CURATION }
            )
            ManagerTab.CURATION -> ManagerCurationScreen(modifier = Modifier.padding(padding))
            ManagerTab.PROJECTS -> ManagerProjectsScreen(modifier = Modifier.padding(padding))
            ManagerTab.GUIDELINES -> ManagerGuidelinesScreen(modifier = Modifier.padding(padding))
            ManagerTab.PROFILE -> ManagerProfileScreen(
                modifier = Modifier.padding(padding),
                onLogout = { sessionViewModel.logout(onLogout) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ManagerTopBar(title: String, onLogout: () -> Unit) {
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
private fun topBarTitle(tab: ManagerTab): String = when (tab) {
    ManagerTab.HOME -> stringResource(R.string.manager_nav_home)
    ManagerTab.CURATION -> stringResource(R.string.manager_nav_curation)
    ManagerTab.PROJECTS -> stringResource(R.string.manager_nav_projects)
    ManagerTab.GUIDELINES -> stringResource(R.string.manager_nav_guidelines)
    ManagerTab.PROFILE -> stringResource(R.string.operator_nav_profile)
}
