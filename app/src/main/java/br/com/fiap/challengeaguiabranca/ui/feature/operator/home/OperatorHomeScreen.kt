package br.com.fiap.challengeaguiabranca.ui.feature.operator.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.com.fiap.challengeaguiabranca.R
import br.com.fiap.challengeaguiabranca.domain.model.IdeaCategory
import br.com.fiap.challengeaguiabranca.ui.feature.operator.components.GuidelineReadOnlyCard
import br.com.fiap.challengeaguiabranca.ui.feature.operator.profile.OperatorProfileScreen
import br.com.fiap.challengeaguiabranca.ui.feature.operator.components.InsightDayCard
import br.com.fiap.challengeaguiabranca.ui.feature.operator.components.KpiStatCard
import br.com.fiap.challengeaguiabranca.ui.feature.operator.components.OperatorBottomBar
import br.com.fiap.challengeaguiabranca.ui.feature.operator.components.OperatorWelcomeBanner
import br.com.fiap.challengeaguiabranca.ui.feature.operator.components.QuickActionCard
import br.com.fiap.challengeaguiabranca.ui.feature.operator.components.RecentIdeaItem
import br.com.fiap.challengeaguiabranca.ui.feature.operator.ideas.OperatorIdeasScreen
import br.com.fiap.challengeaguiabranca.ui.feature.operator.ideas.OperatorIdeasViewModel
import br.com.fiap.challengeaguiabranca.ui.feature.operator.strategies.OperatorStrategiesScreen
import br.com.fiap.challengeaguiabranca.ui.feature.shared.collaborators.CollaboratorsChatScreen
import br.com.fiap.challengeaguiabranca.domain.model.UserRole
import br.com.fiap.challengeaguiabranca.ui.theme.RoleThemedScreen
import br.com.fiap.challengeaguiabranca.ui.theme.innovateBackgroundColor
import br.com.fiap.challengeaguiabranca.ui.theme.InnovatePrimary
import br.com.fiap.challengeaguiabranca.ui.theme.InnovateTextPrimary
import br.com.fiap.challengeaguiabranca.ui.theme.InnovateTextSecondary
import br.com.fiap.challengeaguiabranca.ui.util.premiumTabTransform
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun OperatorHomeScreen(
    onLogout: () -> Unit,
    homeViewModel: OperatorHomeViewModel = koinViewModel(),
    ideasViewModel: OperatorIdeasViewModel = koinViewModel()
) {
    val uiState by homeViewModel.uiState.collectAsStateWithLifecycle()
    val selectedTab by homeViewModel.selectedTab.collectAsStateWithLifecycle()
    val overlay by homeViewModel.overlay.collectAsStateWithLifecycle()
    val submitOverlay by homeViewModel.submitOverlay.collectAsStateWithLifecycle()
    val ideasUiState by ideasViewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(overlay, submitOverlay.category) {
        if (overlay == OperatorOverlay.SUBMIT_IDEA) {
            ideasViewModel.openForm(defaultCategory = submitOverlay.category)
        }
    }

    LaunchedEffect(ideasUiState.successMessage) {
        if (ideasUiState.successMessage != null && overlay == OperatorOverlay.SUBMIT_IDEA) {
            homeViewModel.closeOverlay()
            ideasViewModel.clearSuccessMessage()
        }
    }

    val showMainChrome = overlay == OperatorOverlay.NONE
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    RoleThemedScreen(role = UserRole.OPERATOR) {
    Box(modifier = Modifier.fillMaxSize()) {
        if (showMainChrome) {
            ModalNavigationDrawer(
                drawerState = drawerState,
                drawerContent = {
                    OperatorNotificationsDrawerSheet(
                        notifications = uiState.notifications,
                        onClose = {
                            scope.launch { drawerState.close() }
                        }
                    )
                }
            ) {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = innovateBackgroundColor(),
                    topBar = {
                        OperatorTopBar(
                            title = topBarTitleForTab(selectedTab),
                            showNotificationBadge = uiState.hasUnreadNotifications,
                            onNotificationsClick = {
                                homeViewModel.markNotificationsAsRead()
                                scope.launch { drawerState.open() }
                            }
                        )
                    },
                    bottomBar = {
                        OperatorBottomBar(
                            selectedTab = selectedTab,
                            onTabSelected = homeViewModel::selectTab
                        )
                    }
                ) { padding ->
                    AnimatedContent(
                        targetState = selectedTab,
                        transitionSpec = premiumTabTransform(),
                        label = "operator-tabs"
                    ) { tab ->
                        when (tab) {
                            OperatorTab.HOME -> OperatorHomeContent(
                                uiState = uiState,
                                modifier = Modifier.padding(padding),
                                onNewIdeaClick = { homeViewModel.openSubmitIdea(IdeaCategory.PROCESS) },
                                onReportProblemClick = { homeViewModel.openSubmitIdea(IdeaCategory.OTHER) },
                            onCollaboratorsClick = homeViewModel::openCollaborators,
                                onViewAllIdeasClick = homeViewModel::openAllIdeas,
                                onRetryInsight = homeViewModel::loadInsight
                            )
                            OperatorTab.IDEAS -> OperatorIdeasScreen(
                                modifier = Modifier.padding(padding)
                            )
                            OperatorTab.PROFILE -> OperatorProfileScreen(
                                modifier = Modifier.padding(padding),
                                onLogout = { homeViewModel.logout(onLogout) }
                            )
                            OperatorTab.STRATEGIES -> OperatorStrategiesScreen(
                                modifier = Modifier.padding(padding)
                            )
                        }
                    }
                }
            }
        }

        when (overlay) {
            OperatorOverlay.ALL_IDEAS -> OperatorAllIdeasScreen(
                ideas = uiState.allIdeas,
                onBack = homeViewModel::closeOverlay
            )
            OperatorOverlay.SUBMIT_IDEA -> OperatorSubmitIdeaScreen(
                category = submitOverlay.category,
                form = ideasUiState.form,
                isSubmitting = ideasUiState.isSubmitting,
                errorMessage = ideasUiState.errorMessage,
                onTitleChange = ideasViewModel::onTitleChange,
                onDescriptionChange = ideasViewModel::onDescriptionChange,
                onCategoryChange = ideasViewModel::onCategoryChange,
                onSubmit = ideasViewModel::submitIdea,
                onBack = {
                    ideasViewModel.closeForm()
                    homeViewModel.closeOverlay()
                }
            )
            OperatorOverlay.COLLABORATORS -> CollaboratorsChatScreen(
                currentRole = "Operador",
                onBack = homeViewModel::closeOverlay
            )
            OperatorOverlay.NONE -> Unit
        }
    }
    }
}

@Composable
private fun topBarTitleForTab(tab: OperatorTab): String {
    val resId = when (tab) {
        OperatorTab.HOME -> R.string.operator_nav_home
        OperatorTab.STRATEGIES -> R.string.operator_nav_strategies
        OperatorTab.IDEAS -> R.string.operator_nav_ideas
        OperatorTab.PROFILE -> R.string.operator_nav_profile
    }
    return stringResource(resId)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun OperatorTopBar(
    title: String,
    showNotificationBadge: Boolean,
    onNotificationsClick: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                fontWeight = FontWeight.Bold
            )
        },
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
                if (showNotificationBadge) {
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
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = innovateBackgroundColor(),
            titleContentColor = InnovateTextPrimary
        )
    )
}

@Composable
private fun OperatorHomeContent(
    uiState: OperatorHomeUiState,
    onNewIdeaClick: () -> Unit,
    onReportProblemClick: () -> Unit,
    onCollaboratorsClick: () -> Unit,
    onViewAllIdeasClick: () -> Unit,
    onRetryInsight: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (uiState.isLoading) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = InnovatePrimary)
        }
        return
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Spacer(modifier = Modifier.height(4.dp)) }

        item {
            OperatorWelcomeBanner(
                userFirstName = uiState.userFirstName,
                level = uiState.gamificationLevel,
                points = uiState.gamificationPoints,
                progressFraction = uiState.gamification.progressFraction
            )
        }

        item {
            Text(
                text = stringResource(R.string.operator_quick_actions),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QuickActionCard(
                    title = stringResource(R.string.operator_action_new_idea),
                    icon = Icons.Default.Lightbulb,
                    onClick = onNewIdeaClick,
                    modifier = Modifier.weight(1f)
                )
                QuickActionCard(
                    title = stringResource(R.string.operator_action_report),
                    icon = Icons.Default.ReportProblem,
                    onClick = onReportProblemClick,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QuickActionCard(
                    title = "Colaboradores",
                    icon = Icons.Default.Groups,
                    onClick = onCollaboratorsClick,
                    modifier = Modifier.weight(1f)
                )
                QuickActionCard(
                    title = stringResource(R.string.operator_view_all),
                    icon = Icons.Default.Lightbulb,
                    onClick = onViewAllIdeasClick,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                KpiStatCard(
                    title = stringResource(R.string.operator_kpi_submitted),
                    value = uiState.ideasSubmittedCount.toString(),
                    trend = uiState.submittedTrendLabel,
                    icon = Icons.Default.Lightbulb,
                    modifier = Modifier.weight(1f)
                )
                KpiStatCard(
                    title = stringResource(R.string.operator_kpi_approved),
                    value = uiState.ideasApprovedCount.toString(),
                    trend = uiState.approvedTrendLabel,
                    icon = Icons.Default.CheckCircle,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            InsightDayCard(
                message = uiState.insight?.message,
                isLoading = uiState.insightLoading,
                error = uiState.insightError,
                onRetry = onRetryInsight
            )
        }

        item {
            GuidelineReadOnlyCard(guideline = uiState.featuredGuideline)
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.operator_recent_ideas),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                TextButton(onClick = onViewAllIdeasClick) {
                    Text(
                        text = stringResource(R.string.operator_view_all),
                        color = InnovatePrimary
                    )
                }
            }
        }

        if (uiState.recentIdeas.isEmpty()) {
            item {
                Text(
                    text = stringResource(R.string.operator_no_ideas),
                    color = InnovateTextSecondary,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        } else {
            items(uiState.recentIdeas, key = { it.id }) { idea ->
                RecentIdeaItem(idea = idea)
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}
