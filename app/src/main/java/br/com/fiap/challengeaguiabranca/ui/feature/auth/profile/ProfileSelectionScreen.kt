package br.com.fiap.challengeaguiabranca.ui.feature.auth.profile

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.com.fiap.challengeaguiabranca.R
import br.com.fiap.challengeaguiabranca.domain.model.UserRole
import br.com.fiap.challengeaguiabranca.ui.theme.InnovateSurface
import br.com.fiap.challengeaguiabranca.ui.theme.InnovateBackground
import br.com.fiap.challengeaguiabranca.ui.theme.InnovateLeaderPurple
import br.com.fiap.challengeaguiabranca.ui.theme.InnovateManagerPink
import br.com.fiap.challengeaguiabranca.ui.theme.InnovateOperatorBlue
import br.com.fiap.challengeaguiabranca.ui.theme.InnovatePrimary
import br.com.fiap.challengeaguiabranca.ui.theme.InnovateTextPrimary
import br.com.fiap.challengeaguiabranca.ui.theme.InnovateTextSecondary
import org.koin.androidx.compose.koinViewModel

@Composable
fun ProfileSelectionScreen(
    onNavigateToHome: (String) -> Unit,
    viewModel: ProfileSelectionViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is ProfileSelectionEvent.NavigateToHome -> onNavigateToHome(event.route)
            }
        }
    }

    ProfileSelectionContent(
        uiState = uiState,
        onProfileSelected = viewModel::onProfileSelected,
        onRetry = viewModel::loadUsers
    )
}

@Composable
private fun ProfileSelectionContent(
    uiState: ProfileSelectionUiState,
    onProfileSelected: (UserRole) -> Unit,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(InnovateBackground)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 32.dp)
    ) {
        Text(
            text = stringResource(R.string.profile_title),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = InnovateTextPrimary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.profile_subtitle),
            style = MaterialTheme.typography.bodyLarge,
            color = InnovateTextSecondary
        )
        Spacer(modifier = Modifier.height(28.dp))

        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = InnovatePrimary)
                }
            }
            uiState.errorMessage != null -> {
                Text(
                    text = uiState.errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
                TextButton(onClick = onRetry) {
                    Text(stringResource(R.string.profile_retry))
                }
            }
            else -> {
                if (uiState.isSaving) {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = InnovatePrimary)
                    }
                } else {
                    ProfileCard(
                        title = stringResource(R.string.profile_operator_title),
                        description = stringResource(R.string.profile_operator_desc),
                        icon = Icons.Default.Person,
                        gradient = listOf(InnovateOperatorBlue, InnovateOperatorBlue.copy(alpha = 0.8f)),
                        onClick = { onProfileSelected(UserRole.OPERATOR) }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    ProfileCard(
                        title = stringResource(R.string.profile_manager_title),
                        description = stringResource(R.string.profile_manager_desc),
                        icon = Icons.Default.BusinessCenter,
                        gradient = listOf(InnovateManagerPink, InnovatePrimary),
                        onClick = { onProfileSelected(UserRole.MANAGER) }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    ProfileCard(
                        title = stringResource(R.string.profile_leader_title),
                        description = stringResource(R.string.profile_leader_desc),
                        icon = Icons.Default.WorkspacePremium,
                        gradient = listOf(InnovateLeaderPurple, InnovatePrimary),
                        onClick = { onProfileSelected(UserRole.LEADER) }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = stringResource(R.string.profile_footer),
            style = MaterialTheme.typography.bodySmall,
            color = InnovateTextSecondary,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun ProfileCard(
    title: String,
    description: String,
    icon: ImageVector,
    gradient: List<Color>,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = InnovateSurface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Brush.linearGradient(gradient)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White
                )
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = InnovateTextSecondary
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = InnovateTextSecondary
            )
        }
    }
}
