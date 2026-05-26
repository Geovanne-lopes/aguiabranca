package br.com.fiap.challengeaguiabranca.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import br.com.fiap.challengeaguiabranca.R
import br.com.fiap.challengeaguiabranca.ui.theme.InnovateOnPrimary
import br.com.fiap.challengeaguiabranca.ui.theme.InnovatePrimary
import br.com.fiap.challengeaguiabranca.ui.theme.InnovateTextSecondary
import br.com.fiap.challengeaguiabranca.ui.util.GamificationState

data class ProfileStatRow(
    val label: String,
    val value: String
)

@Composable
fun RoleProfileContent(
    userName: String,
    userEmail: String,
    roleLabel: String,
    stats: List<ProfileStatRow>,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier,
    gamification: GamificationState? = null
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                shape = CircleShape,
                color = InnovatePrimary.copy(alpha = 0.15f)
            ) {
                Text(
                    text = userName.take(1).uppercase(),
                    modifier = Modifier.padding(24.dp),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = InnovatePrimary
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(userName, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text(userEmail, color = InnovateTextSecondary, style = MaterialTheme.typography.bodyMedium)
            Text(roleLabel, color = InnovatePrimary, style = MaterialTheme.typography.labelLarge)
        }

        gamification?.let { GamificationCard(state = it) }

        if (stats.isNotEmpty()) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    stringResource(R.string.profile_stats_title),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                stats.forEach { row ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(row.label, color = InnovateTextSecondary)
                        Text(row.value, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }

        Text(
            stringResource(R.string.home_logout_hint),
            color = InnovateTextSecondary,
            style = MaterialTheme.typography.bodySmall
        )

        Button(
            onClick = onLogout,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = InnovatePrimary)
        ) {
            Text(stringResource(R.string.home_logout_account), color = InnovateOnPrimary)
        }
    }
}
