package br.com.fiap.challengeaguiabranca.ui.feature.manager.curation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.com.fiap.challengeaguiabranca.R
import br.com.fiap.challengeaguiabranca.domain.catalog.MockOperatorsCatalog
import br.com.fiap.challengeaguiabranca.domain.model.Idea
import br.com.fiap.challengeaguiabranca.domain.model.IdeaStatus
import br.com.fiap.challengeaguiabranca.ui.theme.InnovateOnPrimary
import br.com.fiap.challengeaguiabranca.ui.theme.InnovatePrimary
import br.com.fiap.challengeaguiabranca.ui.theme.InnovateTextSecondary
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun ManagerCurationScreen(
    modifier: Modifier = Modifier,
    viewModel: ManagerCurationViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }
    var animatingId by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(uiState.actionMessage) {
        uiState.actionMessage?.let {
            snackbar.showSnackbar(it)
            viewModel.clearMessage()
        }
    }

    fun animateAndAct(idea: Idea, newStatus: IdeaStatus) {
        scope.launch {
            animatingId = idea.id
            delay(620)
            viewModel.updateStatus(idea, newStatus)
            animatingId = null
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        when {
            uiState.isLoading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                    CircularProgressIndicator(color = InnovatePrimary)
                }
            }
            uiState.ideas.isEmpty() -> {
                Box(Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                    Text(stringResource(R.string.manager_curation_empty), color = InnovateTextSecondary)
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = stringResource(R.string.manager_curation_title),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    items(uiState.ideas, key = { it.id }) { idea ->
                        val isAnimatingOut = animatingId == idea.id
                        AnimatedVisibility(
                            visible = !isAnimatingOut,
                            enter = fadeIn(),
                            exit = slideOutVertically(
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessLow
                                ),
                                targetOffsetY = { -it - 220 }
                            ) + scaleOut(
                                animationSpec = tween(520),
                                targetScale = 0.72f
                            ) + fadeOut(animationSpec = tween(520)) + shrinkVertically(animationSpec = tween(520))
                        ) {
                            CurationIdeaCard(
                                idea = idea,
                                onApprove = { animateAndAct(idea, IdeaStatus.APPROVED) },
                                onReject = { animateAndAct(idea, IdeaStatus.REJECTED) },
                                onPrioritize = { animateAndAct(idea, IdeaStatus.PRIORITIZED) }
                            )
                        }
                    }
                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }
            }
        }
        SnackbarHost(hostState = snackbar, modifier = Modifier.align(androidx.compose.ui.Alignment.BottomCenter))
    }
}

@Composable
private fun CurationIdeaCard(
    idea: Idea,
    onApprove: () -> Unit,
    onReject: () -> Unit,
    onPrioritize: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(idea.title, fontWeight = FontWeight.Bold)
            Text(
                stringResource(R.string.manager_idea_author, MockOperatorsCatalog.resolveName(idea.authorId)),
                style = MaterialTheme.typography.labelMedium,
                color = InnovatePrimary
            )
            Text(idea.description, style = MaterialTheme.typography.bodySmall, color = InnovateTextSecondary)
            Spacer(modifier = Modifier.height(4.dp))
            Text("${idea.category.label} · ${idea.status.name}", style = MaterialTheme.typography.labelSmall)
            if (idea.status == IdeaStatus.PENDING) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = onApprove,
                        colors = ButtonDefaults.buttonColors(containerColor = InnovatePrimary),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(stringResource(R.string.manager_action_approve), color = InnovateOnPrimary)
                    }
                    OutlinedButton(onClick = onReject, modifier = Modifier.weight(1f)) {
                        Text(stringResource(R.string.manager_action_reject))
                    }
                    OutlinedButton(onClick = onPrioritize, modifier = Modifier.weight(1f)) {
                        Text(stringResource(R.string.manager_action_prioritize))
                    }
                }
            }
        }
    }
}
