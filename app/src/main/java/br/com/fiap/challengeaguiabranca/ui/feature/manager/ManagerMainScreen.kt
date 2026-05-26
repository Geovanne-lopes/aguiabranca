package br.com.fiap.challengeaguiabranca.ui.feature.manager

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.com.fiap.challengeaguiabranca.R
import br.com.fiap.challengeaguiabranca.ui.feature.manager.components.ManagerBottomBar
import br.com.fiap.challengeaguiabranca.ui.feature.manager.curation.ManagerCurationScreen
import br.com.fiap.challengeaguiabranca.ui.feature.manager.guidelines.ManagerGuidelinesScreen
import br.com.fiap.challengeaguiabranca.ui.feature.manager.home.ManagerDashboardScreen
import br.com.fiap.challengeaguiabranca.ui.feature.manager.idea.ManagerCreateIdeaScreen
import br.com.fiap.challengeaguiabranca.ui.feature.manager.notifications.ManagerNotificationsDrawerSheet
import br.com.fiap.challengeaguiabranca.ui.feature.manager.profile.ManagerProfileScreen
import br.com.fiap.challengeaguiabranca.ui.feature.manager.projects.ManagerProjectsScreen
import br.com.fiap.challengeaguiabranca.ui.feature.manager.suggestion.ManagerSuggestionScreen
import br.com.fiap.challengeaguiabranca.ui.feature.manager.team.ManagerTeamScreen
import br.com.fiap.challengeaguiabranca.ui.feature.shared.collaborators.CollaboratorsChatScreen
import br.com.fiap.challengeaguiabranca.ui.feature.shared.RoleHomeViewModel
import br.com.fiap.challengeaguiabranca.ui.theme.InnovateBackground
import br.com.fiap.challengeaguiabranca.ui.theme.InnovateTextPrimary
import br.com.fiap.challengeaguiabranca.ui.util.premiumTabTransform
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun ManagerMainScreen(
    onLogout: () -> Unit,
    mainViewModel: ManagerMainViewModel = koinViewModel(),
    sessionViewModel: RoleHomeViewModel = koinViewModel()
) {
    val mainState by mainViewModel.uiState.collectAsStateWithLifecycle()
    val overlay by mainViewModel.overlay.collectAsStateWithLifecycle()
    var selectedTab by remember { mutableStateOf(ManagerTab.HOME) }
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize()) {
        if (overlay == ManagerOverlay.NONE) {
            ModalNavigationDrawer(
                drawerState = drawerState,
                drawerContent = {
                    ManagerNotificationsDrawerSheet(
                        notifications = mainState.notifications,
                        onClose = { scope.launch { drawerState.close() } }
                    )
                }
            ) {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = InnovateBackground,
                    topBar = {
                        ManagerTopBar(
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
                        ManagerBottomBar(
                            selectedTab = selectedTab,
                            onTabSelected = { selectedTab = it }
                        )
                    }
                ) { padding ->
                    AnimatedContent(
                        targetState = selectedTab,
                        transitionSpec = premiumTabTransform(),
                        label = "manager-tabs"
                    ) { tab ->
                        when (tab) {
                            ManagerTab.HOME -> ManagerDashboardScreen(
                                modifier = Modifier.padding(padding),
                                onOpenCuration = { selectedTab = ManagerTab.CURATION },
                                onCreateIdea = mainViewModel::openCreateIdea,
                                onSendSuggestion = mainViewModel::openSuggestion,
                            onOpenCollaborators = mainViewModel::openCollaborators,
                                onViewTeam = mainViewModel::openTeam,
                                topOperators = mainState.topOperators
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
            }
        }

        when (overlay) {
            ManagerOverlay.TEAM -> ManagerTeamScreen(
                operators = mainState.allOperators,
                onBack = mainViewModel::closeOverlay
            )
            ManagerOverlay.SUGGESTION -> ManagerSuggestionScreen(
                onBack = mainViewModel::closeOverlay
            )
            ManagerOverlay.CREATE_IDEA -> ManagerCreateIdeaScreen(
                onBack = mainViewModel::closeOverlay,
                onSuccess = mainViewModel::closeOverlay
            )
            ManagerOverlay.COLLABORATORS -> CollaboratorsChatScreen(
                currentRole = "Gestor",
                onBack = mainViewModel::closeOverlay
            )
            ManagerOverlay.NONE -> Unit
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ManagerTopBar(
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
                            .background(androidx.compose.material3.MaterialTheme.colorScheme.error)
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
private fun topBarTitle(tab: ManagerTab): String = when (tab) {
    ManagerTab.HOME -> stringResource(R.string.manager_nav_home)
    ManagerTab.CURATION -> stringResource(R.string.manager_nav_curation)
    ManagerTab.PROJECTS -> stringResource(R.string.manager_nav_projects)
    ManagerTab.GUIDELINES -> stringResource(R.string.manager_nav_guidelines)
    ManagerTab.PROFILE -> stringResource(R.string.operator_nav_profile)
}
