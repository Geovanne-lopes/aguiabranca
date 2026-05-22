package br.com.fiap.challengeaguiabranca.ui.feature.leader.tracking

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.com.fiap.challengeaguiabranca.R
import br.com.fiap.challengeaguiabranca.domain.model.Project
import br.com.fiap.challengeaguiabranca.ui.theme.InnovatePrimary
import br.com.fiap.challengeaguiabranca.ui.theme.InnovateTextSecondary
import br.com.fiap.challengeaguiabranca.ui.util.formatCurrency
import br.com.fiap.challengeaguiabranca.ui.util.formatDate
import org.koin.androidx.compose.koinViewModel

@Composable
fun LeaderTrackingScreen(
    modifier: Modifier = Modifier,
    viewModel: LeaderTrackingViewModel = koinViewModel()
) {
    val projects by viewModel.projects.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    if (isLoading) {
        Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = InnovatePrimary)
        }
        return
    }

    if (projects.isEmpty()) {
        Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(stringResource(R.string.leader_tracking_empty), color = InnovateTextSecondary)
        }
        return
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                stringResource(R.string.leader_tracking_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
        items(projects, key = { it.id }) { project ->
            TrackingProjectCard(project)
        }
        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}

@Composable
private fun TrackingProjectCard(project: Project) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(project.title, fontWeight = FontWeight.Bold)
            Text(project.status.label, style = MaterialTheme.typography.labelMedium, color = InnovatePrimary)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                stringResource(
                    R.string.leader_tracking_investment,
                    formatCurrency(project.investmentAmount)
                ),
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                stringResource(
                    R.string.leader_tracking_profit,
                    formatCurrency(project.obtainedProfit)
                ),
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                stringResource(
                    R.string.leader_tracking_roi,
                    "%.0f".format(project.roiPercent)
                ),
                style = MaterialTheme.typography.bodySmall,
                color = InnovateTextSecondary
            )
            Text(
                stringResource(
                    R.string.leader_tracking_productivity,
                    "%.0f".format(project.productivityGainPercent)
                ),
                style = MaterialTheme.typography.bodySmall,
                color = InnovateTextSecondary
            )
            Text(
                stringResource(R.string.leader_tracking_deadline, formatDate(project.deadlineEpochMillis)),
                style = MaterialTheme.typography.labelSmall,
                color = InnovateTextSecondary
            )
        }
    }
}
