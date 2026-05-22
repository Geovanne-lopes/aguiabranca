package br.com.fiap.challengeaguiabranca.ui.feature.operator.home

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
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.com.fiap.challengeaguiabranca.R
import br.com.fiap.challengeaguiabranca.ui.feature.operator.components.GuidelineReadOnlyCard
import br.com.fiap.challengeaguiabranca.ui.feature.operator.components.InsightDayCard
import br.com.fiap.challengeaguiabranca.ui.feature.operator.components.KpiStatCard
import br.com.fiap.challengeaguiabranca.ui.feature.operator.components.OperatorBottomBar
import br.com.fiap.challengeaguiabranca.ui.feature.operator.components.OperatorWelcomeBanner
import br.com.fiap.challengeaguiabranca.ui.feature.operator.components.QuickActionCard
import br.com.fiap.challengeaguiabranca.ui.feature.operator.components.RecentIdeaItem
import br.com.fiap.challengeaguiabranca.domain.model.IdeaCategory
import br.com.fiap.challengeaguiabranca.ui.components.ProfileStatRow
import br.com.fiap.challengeaguiabranca.ui.components.RoleProfileContent
import br.com.fiap.challengeaguiabranca.ui.feature.operator.ideas.OperatorIdeasScreen
import br.com.fiap.challengeaguiabranca.ui.feature.operator.ideas.OperatorIdeasViewModel
import br.com.fiap.challengeaguiabranca.ui.feature.operator.strategies.OperatorStrategiesScreen
import br.com.fiap.challengeaguiabranca.ui.theme.InnovateBackground
import br.com.fiap.challengeaguiabranca.ui.theme.InnovatePrimary
import br.com.fiap.challengeaguiabranca.ui.theme.InnovateTextPrimary
import br.com.fiap.challengeaguiabranca.ui.theme.InnovateTextSecondary
import org.koin.androidx.compose.koinViewModel

@Composable
fun OperatorHomeScreen(
    onLogout: () -> Unit,
    homeViewModel: OperatorHomeViewModel = koinViewModel(),
    ideasViewModel: OperatorIdeasViewModel = koinViewModel()
) {
    val uiState by homeViewModel.uiState.collectAsStateWithLifecycle()
    val selectedTab by homeViewModel.selectedTab.collectAsStateWithLifecycle()

    val openIdeasTab: (IdeaCategory) -> Unit = { category ->
        homeViewModel.selectTab(OperatorTab.IDEAS)
        ideasViewModel.openForm(defaultCategory = category)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = InnovateBackground,
        topBar = {
            OperatorTopBar(
                title = topBarTitleForTab(selectedTab),
                showNotificationBadge = selectedTab == OperatorTab.HOME
            )
        },
        bottomBar = {
            OperatorBottomBar(
                selectedTab = selectedTab,
                onTabSelected = homeViewModel::selectTab
            )
        }
    ) { padding ->
        when (selectedTab) {
            OperatorTab.HOME -> OperatorHomeContent(
                uiState = uiState,
                modifier = Modifier.padding(padding),
                onNewIdeaClick = { openIdeasTab(IdeaCategory.PROCESS) },
                onReportProblemClick = { openIdeasTab(IdeaCategory.OTHER) },
                onViewAllIdeasClick = { homeViewModel.selectTab(OperatorTab.IDEAS) },
                onRetryInsight = homeViewModel::loadInsight
            )
            OperatorTab.IDEAS -> OperatorIdeasScreen(
                modifier = Modifier.padding(padding)
            )
            OperatorTab.PROFILE -> RoleProfileContent(
                modifier = Modifier.padding(padding),
                userName = uiState.userFullName,
                userEmail = uiState.userEmail,
                roleLabel = stringResource(R.string.profile_operator_title),
                gamification = uiState.gamification,
                stats = listOf(
                    ProfileStatRow(
                        stringResource(R.string.operator_kpi_submitted),
                        uiState.ideasSubmittedCount.toString()
                    ),
                    ProfileStatRow(
                        stringResource(R.string.operator_kpi_approved),
                        uiState.ideasApprovedCount.toString()
                    )
                ),
                onLogout = { homeViewModel.logout(onLogout) }
            )
            OperatorTab.STRATEGIES -> OperatorStrategiesScreen(
                modifier = Modifier.padding(padding)
            )
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
    showNotificationBadge: Boolean
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
                IconButton(onClick = { }) {
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
            containerColor = InnovateBackground,
            titleContentColor = InnovateTextPrimary
        )
    )
}

@Composable
private fun OperatorHomeContent(
    uiState: OperatorHomeUiState,
    onNewIdeaClick: () -> Unit,
    onReportProblemClick: () -> Unit,
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
