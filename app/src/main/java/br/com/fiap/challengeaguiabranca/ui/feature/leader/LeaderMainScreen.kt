package br.com.fiap.challengeaguiabranca.ui.feature.leader

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.com.fiap.challengeaguiabranca.R
import br.com.fiap.challengeaguiabranca.ui.feature.leader.components.LeaderBottomBar
import br.com.fiap.challengeaguiabranca.ui.feature.leader.guidelines.LeaderGuidelinesScreen
import br.com.fiap.challengeaguiabranca.ui.feature.leader.home.LeaderDashboardScreen
import br.com.fiap.challengeaguiabranca.ui.feature.leader.notifications.LeaderNotificationsDrawerSheet
import br.com.fiap.challengeaguiabranca.ui.feature.leader.profile.LeaderProfileScreen
import br.com.fiap.challengeaguiabranca.ui.feature.leader.team.LeaderTeamSection
import br.com.fiap.challengeaguiabranca.ui.feature.leader.tracking.LeaderTrackingScreen
import br.com.fiap.challengeaguiabranca.ui.feature.manager.suggestion.ManagerSuggestionScreen
import br.com.fiap.challengeaguiabranca.ui.feature.shared.RoleHomeViewModel
import br.com.fiap.challengeaguiabranca.ui.feature.shared.collaborators.CollaboratorsChatScreen
import br.com.fiap.challengeaguiabranca.ui.theme.InnovateBackground
import br.com.fiap.challengeaguiabranca.ui.theme.InnovateTextPrimary
import br.com.fiap.challengeaguiabranca.ui.util.premiumTabTransform
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun LeaderMainScreen(
    onLogout: () -> Unit,
    mainViewModel: LeaderMainViewModel = koinViewModel(),
    sessionViewModel: RoleHomeViewModel = koinViewModel()
) {
    val mainState by mainViewModel.uiState.collectAsStateWithLifecycle()
    val overlay by mainViewModel.overlay.collectAsStateWithLifecycle()
    var selectedTab by remember { mutableStateOf(LeaderTab.HOME) }
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize()) {
        if (overlay == LeaderOverlay.NONE) {
            ModalNavigationDrawer(
                drawerState = drawerState,
                drawerContent = {
                    LeaderNotificationsDrawerSheet(
                        notifications = mainState.notifications,
                        onClose = { scope.launch { drawerState.close() } }
                    )
                }
            ) {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = InnovateBackground,
                    topBar = {
                        LeaderTopBar(
                            title = topBarTitle(selectedTab),
                            showBadge = mainState.hasUnreadNotifications,
                            onNotificationsClick = {
                                mainViewModel.markNotificationsAsRead()
                                scope.launch { drawerState.open() }
                            },
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
                    AnimatedContent(
                        targetState = selectedTab,
                        transitionSpec = premiumTabTransform(),
                        label = "leader-tabs"
                    ) { tab ->
                        when (tab) {
                            LeaderTab.HOME -> LeaderDashboardScreen(
                                modifier = Modifier.padding(padding),
                                monthlyRanking = mainState.monthlyRanking,
                                onSendSuggestion = mainViewModel::openSuggestion,
                                onViewTeam = { selectedTab = LeaderTab.TEAM },
                                onCreateGuideline = { selectedTab = LeaderTab.GUIDELINES },
                                onOpenCollaborators = mainViewModel::openCollaborators
                            )
                            LeaderTab.GUIDELINES -> LeaderGuidelinesScreen(modifier = Modifier.padding(padding))
                            LeaderTab.TEAM -> LeaderTeamSection(
                                monthlyRanking = mainState.monthlyRanking,
                                overallRanking = mainState.overallRanking,
                                modifier = Modifier.padding(padding)
                            )
                            LeaderTab.TRACKING -> LeaderTrackingScreen(modifier = Modifier.padding(padding))
                            LeaderTab.PROFILE -> LeaderProfileScreen(
                                modifier = Modifier.padding(padding),
                                onLogout = { sessionViewModel.logout(onLogout) }
                            )
                        }
                    }
                }
            }
        }

        when (overlay) {
            LeaderOverlay.SUGGESTION -> ManagerSuggestionScreen(onBack = mainViewModel::closeOverlay)
            LeaderOverlay.COLLABORATORS -> CollaboratorsChatScreen(
                currentRole = "Líder",
                onBack = mainViewModel::closeOverlay
            )
            LeaderOverlay.NONE -> Unit
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LeaderTopBar(
    title: String,
    showBadge: Boolean,
    onNotificationsClick: () -> Unit,
    onLogout: () -> Unit
) {
    TopAppBar(
        title = { Text(title, fontWeight = FontWeight.Bold) },
        navigationIcon = {
            IconButton(onClick = { }) {
                Icon(Icons.Default.Menu, contentDescription = null)
            }
        },
        actions = {
            Box {
                IconButton(onClick = onNotificationsClick) {
                    Icon(Icons.Default.Notifications, contentDescription = null)
                }
                if (showBadge) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(top = 10.dp, end = 10.dp)
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.error)
                    )
                }
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
    LeaderTab.TEAM -> stringResource(R.string.leader_nav_team)
    LeaderTab.TRACKING -> stringResource(R.string.leader_nav_tracking)
    LeaderTab.PROFILE -> stringResource(R.string.operator_nav_profile)
}
