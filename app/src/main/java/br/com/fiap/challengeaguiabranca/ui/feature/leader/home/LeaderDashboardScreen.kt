package br.com.fiap.challengeaguiabranca.ui.feature.leader.home

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.com.fiap.challengeaguiabranca.R
import br.com.fiap.challengeaguiabranca.ui.components.SimpleBarChart
import br.com.fiap.challengeaguiabranca.ui.feature.operator.components.KpiStatCard
import br.com.fiap.challengeaguiabranca.ui.theme.InnovateOnPrimary
import br.com.fiap.challengeaguiabranca.ui.theme.InnovatePrimary
import br.com.fiap.challengeaguiabranca.ui.theme.InnovatePrimaryDark
import br.com.fiap.challengeaguiabranca.ui.theme.InnovateTextSecondary
import br.com.fiap.challengeaguiabranca.ui.util.formatCurrency
import org.koin.androidx.compose.koinViewModel

@Composable
fun LeaderDashboardScreen(
    modifier: Modifier = Modifier,
    viewModel: LeaderDashboardViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    if (uiState.isLoading) {
        Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
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
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Brush.horizontalGradient(listOf(InnovatePrimary, InnovatePrimaryDark)))
                        .padding(20.dp)
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
                            color = InnovateOnPrimary.copy(alpha = 0.9f)
                        )
                    }
                }
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

        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}
