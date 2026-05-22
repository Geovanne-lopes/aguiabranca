package br.com.fiap.challengeaguiabranca.ui.feature.manager.profile

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.com.fiap.challengeaguiabranca.R
import br.com.fiap.challengeaguiabranca.ui.components.ProfileStatRow
import br.com.fiap.challengeaguiabranca.ui.components.RoleProfileContent
import br.com.fiap.challengeaguiabranca.ui.theme.InnovatePrimary
import org.koin.androidx.compose.koinViewModel

@Composable
fun ManagerProfileScreen(
    onLogout: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ManagerProfileViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    if (uiState.isLoading) {
        Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = InnovatePrimary)
        }
        return
    }

    RoleProfileContent(
        modifier = modifier,
        userName = uiState.userName,
        userEmail = uiState.userEmail,
        roleLabel = stringResource(R.string.profile_manager_title),
        gamification = null,
        stats = listOf(
            ProfileStatRow(
                stringResource(R.string.manager_profile_pending),
                uiState.pendingIdeas.toString()
            ),
            ProfileStatRow(
                stringResource(R.string.manager_profile_active_projects),
                uiState.activeProjects.toString()
            ),
            ProfileStatRow(
                stringResource(R.string.manager_profile_total_ideas),
                uiState.totalIdeas.toString()
            )
        ),
        onLogout = onLogout
    )
}
