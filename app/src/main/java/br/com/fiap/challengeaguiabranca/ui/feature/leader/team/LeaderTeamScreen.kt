package br.com.fiap.challengeaguiabranca.ui.feature.leader.team

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import br.com.fiap.challengeaguiabranca.R
import br.com.fiap.challengeaguiabranca.domain.model.OperatorActivity
import br.com.fiap.challengeaguiabranca.ui.theme.InnovateSurface
import br.com.fiap.challengeaguiabranca.ui.theme.InnovateLeaderPurple
import br.com.fiap.challengeaguiabranca.ui.theme.InnovateOnPrimary
import br.com.fiap.challengeaguiabranca.ui.theme.InnovatePrimary
import br.com.fiap.challengeaguiabranca.ui.theme.InnovateTextSecondary

@Composable
fun LeaderTeamSection(
    monthlyRanking: List<OperatorActivity>,
    overallRanking: List<OperatorActivity>,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                stringResource(R.string.leader_team_monthly_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                stringResource(R.string.leader_team_monthly_subtitle),
                style = MaterialTheme.typography.bodySmall,
                color = InnovateTextSecondary
            )
        }
        if (monthlyRanking.isEmpty()) {
            item { Text(stringResource(R.string.manager_team_empty), color = InnovateTextSecondary) }
        } else {
            itemsIndexed(monthlyRanking, key = { _, op -> "m-${op.authorId}" }) { index, operator ->
                RankCard(rank = index + 1, operator = operator, highlight = true)
            }
        }
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                stringResource(R.string.leader_team_overall_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
        if (overallRanking.isEmpty()) {
            item { Text(stringResource(R.string.manager_team_empty), color = InnovateTextSecondary) }
        } else {
            itemsIndexed(overallRanking, key = { _, op -> "o-${op.authorId}" }) { index, operator ->
                RankCard(rank = index + 1, operator = operator, highlight = false)
            }
        }
        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}

@Composable
private fun RankCard(rank: Int, operator: OperatorActivity, highlight: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = InnovateSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = if (rank <= 3 && highlight) InnovateLeaderPurple else InnovatePrimary.copy(alpha = 0.15f),
                modifier = Modifier.size(40.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        "#$rank",
                        fontWeight = FontWeight.Bold,
                        color = if (rank <= 3 && highlight) InnovateOnPrimary else InnovatePrimary
                    )
                }
            }
            Column(modifier = Modifier.padding(start = 14.dp).weight(1f)) {
                Text(operator.name, fontWeight = FontWeight.Bold)
                if (operator.email.isNotBlank()) {
                    Text(
                        operator.email,
                        style = MaterialTheme.typography.bodySmall,
                        color = InnovateTextSecondary
                    )
                }
                Text(
                    stringResource(
                        R.string.manager_team_stats,
                        operator.ideasSubmitted,
                        operator.ideasApproved
                    ),
                    style = MaterialTheme.typography.labelMedium,
                    color = InnovateLeaderPurple
                )
            }
        }
    }
}
