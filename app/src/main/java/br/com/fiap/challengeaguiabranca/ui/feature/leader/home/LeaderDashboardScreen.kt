package br.com.fiap.challengeaguiabranca.ui.feature.leader.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.com.fiap.challengeaguiabranca.R
import br.com.fiap.challengeaguiabranca.domain.model.OperatorActivity
import br.com.fiap.challengeaguiabranca.ui.components.SimpleBarChart
import br.com.fiap.challengeaguiabranca.ui.feature.operator.components.KpiStatCard
import br.com.fiap.challengeaguiabranca.ui.theme.InnovateLeaderPurple
import br.com.fiap.challengeaguiabranca.ui.theme.InnovateOnPrimary
import br.com.fiap.challengeaguiabranca.ui.theme.InnovateShapes
import br.com.fiap.challengeaguiabranca.ui.theme.currentRoleAccent
import br.com.fiap.challengeaguiabranca.ui.theme.InnovateTextPrimary
import br.com.fiap.challengeaguiabranca.ui.theme.InnovateTextSecondary
import br.com.fiap.challengeaguiabranca.ui.util.formatCurrency
import org.koin.androidx.compose.koinViewModel

@Composable
fun LeaderDashboardScreen(
    monthlyRanking: List<OperatorActivity>,
    onSendSuggestion: () -> Unit,
    onViewTeam: () -> Unit,
    onCreateGuideline: () -> Unit,
    onOpenCollaborators: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LeaderDashboardViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    if (uiState.isLoading) {
        Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = currentRoleAccent().accent)
        }
        return
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item { Spacer(modifier = Modifier.height(4.dp)) }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            currentRoleAccent().horizontalGradient()
                        )
                        .padding(22.dp)
                ) {
                    Column {
                        Text(
                            stringResource(R.string.leader_greeting, uiState.userFirstName),
                            color = InnovateOnPrimary,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            stringResource(R.string.leader_header_subtitle),
                            color = InnovateOnPrimary.copy(alpha = 0.92f)
                        )
                    }
                }
            }
        }

        item {
            Text(
                stringResource(R.string.leader_quick_actions),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QuickAction(
                    icon = Icons.Default.Campaign,
                    label = stringResource(R.string.leader_quick_new_guideline_hint),
                    onClick = onCreateGuideline,
                    modifier = Modifier.weight(1f)
                )
                QuickAction(
                    icon = Icons.Default.Send,
                    label = stringResource(R.string.leader_quick_send_suggestion),
                    onClick = onSendSuggestion,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QuickAction(
                    icon = Icons.Default.Groups,
                    label = "Colaboradores",
                    onClick = onOpenCollaborators,
                    modifier = Modifier.weight(1f)
                )
                QuickAction(
                    icon = Icons.Default.EmojiEvents,
                    label = stringResource(R.string.leader_view_all),
                    onClick = onViewTeam,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    stringResource(R.string.leader_top_operators),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                TextButton(onClick = onViewTeam) {
                    Text(stringResource(R.string.leader_view_all))
                }
            }
        }

        if (monthlyRanking.isEmpty()) {
            item {
                Text(
                    stringResource(R.string.leader_team_empty_month),
                    color = InnovateTextSecondary
                )
            }
        } else {
            itemsIndexed(monthlyRanking.take(3), key = { _, op -> "top-${op.authorId}" }) { index, op ->
                TopOperatorRow(rank = index + 1, operator = op)
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                KpiStatCard(
                    title = stringResource(R.string.leader_kpi_investment),
                    value = formatCurrency(uiState.summary.totalInvestment),
                    trend = stringResource(R.string.leader_kpi_active, uiState.summary.activeProjectsCount),
                    icon = Icons.Default.TrendingUp,
                    modifier = Modifier.weight(1f)
                )
                KpiStatCard(
                    title = stringResource(R.string.leader_kpi_roi),
                    value = "${"%.0f".format(uiState.summary.overallRoiPercent)}%",
                    trend = stringResource(
                        R.string.leader_kpi_profit,
                        formatCurrency(uiState.summary.totalObtainedProfit)
                    ),
                    icon = Icons.Default.BarChart,
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
                    title = stringResource(R.string.leader_kpi_productivity),
                    value = "${"%.0f".format(uiState.summary.averageProductivityGainPercent)}%",
                    trend = stringResource(
                        R.string.leader_kpi_completed,
                        uiState.summary.completedProjectsCount
                    ),
                    icon = Icons.Default.TrendingUp,
                    modifier = Modifier.weight(1f)
                )
                KpiStatCard(
                    title = stringResource(R.string.leader_kpi_projects),
                    value = (uiState.summary.activeProjectsCount + uiState.summary.completedProjectsCount).toString(),
                    trend = stringResource(R.string.leader_kpi_portfolio_hint),
                    icon = Icons.Default.BarChart,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        stringResource(R.string.leader_chart_status_title),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    SimpleBarChart(
                        values = uiState.statusChartValues,
                        labels = uiState.statusChartLabels,
                        highlightLast = false
                    )
                    if (uiState.statusChartValues.all { it == 0 }) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            stringResource(R.string.leader_chart_empty),
                            color = InnovateTextSecondary,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}

@Composable
private fun QuickAction(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(InnovateLeaderPurple.copy(alpha = 0.14f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = InnovateLeaderPurple)
            }
            Text(
                label,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = InnovateTextPrimary
            )
        }
    }
}

@Composable
private fun TopOperatorRow(rank: Int, operator: OperatorActivity) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = if (rank == 1) Color(0xFFF59E0B)
                else if (rank == 2) Color(0xFF94A3B8)
                else if (rank == 3) Color(0xFFB45309)
                else InnovatePrimary.copy(alpha = 0.15f),
                modifier = Modifier.size(36.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        Icons.Default.EmojiEvents,
                        null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            Column(modifier = Modifier.padding(start = 14.dp).weight(1f)) {
                Text("#$rank ${operator.name}", fontWeight = FontWeight.Bold)
                Text(
                    stringResource(R.string.leader_team_stats, operator.ideasSubmitted, operator.ideasApproved),
                    style = MaterialTheme.typography.bodySmall,
                    color = InnovateTextSecondary
                )
            }
        }
    }
}

