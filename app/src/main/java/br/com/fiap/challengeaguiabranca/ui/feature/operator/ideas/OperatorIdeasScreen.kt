package br.com.fiap.challengeaguiabranca.ui.feature.operator.ideas

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.com.fiap.challengeaguiabranca.R
import br.com.fiap.challengeaguiabranca.domain.model.Idea
import br.com.fiap.challengeaguiabranca.domain.model.IdeaCategory
import br.com.fiap.challengeaguiabranca.domain.model.IdeaStatus
import br.com.fiap.challengeaguiabranca.ui.theme.InnovateSurface
import br.com.fiap.challengeaguiabranca.ui.theme.InnovateOnPrimary
import br.com.fiap.challengeaguiabranca.ui.theme.InnovatePrimary
import br.com.fiap.challengeaguiabranca.ui.theme.InnovateTextSecondary
import org.koin.androidx.compose.koinViewModel

@Composable
fun OperatorIdeasScreen(
    modifier: Modifier = Modifier,
    viewModel: OperatorIdeasViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.successMessage) {
        uiState.successMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearSuccessMessage()
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = InnovatePrimary)
                }
            }
            else -> {
                OperatorIdeasContent(
                    uiState = uiState,
                    onOpenForm = { viewModel.openForm() },
                    onCloseForm = viewModel::closeForm,
                    onTitleChange = viewModel::onTitleChange,
                    onDescriptionChange = viewModel::onDescriptionChange,
                    onCategoryChange = viewModel::onCategoryChange,
                    onSubmit = viewModel::submitIdea
                )
            }
        }

        if (!uiState.form.isVisible && !uiState.isLoading) {
            FloatingActionButton(
                onClick = { viewModel.openForm() },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                containerColor = InnovatePrimary
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = stringResource(R.string.operator_action_new_idea),
                    tint = InnovateOnPrimary
                )
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun OperatorIdeasContent(
    uiState: OperatorIdeasUiState,
    onOpenForm: () -> Unit,
    onCloseForm: () -> Unit,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onCategoryChange: (IdeaCategory) -> Unit,
    onSubmit: () -> Unit
) {
    val sortedIdeas = uiState.ideas.sortedWith(
        compareByDescending<Idea> { idea ->
            when (idea.status) {
                IdeaStatus.APPROVED, IdeaStatus.PRIORITIZED -> 2
                IdeaStatus.PENDING -> 1
                IdeaStatus.REJECTED -> 0
            }
        }.thenByDescending { it.createdAtEpochMillis }
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { Spacer(modifier = Modifier.height(8.dp)) }

        if (uiState.form.isVisible) {
            item {
                OperatorIdeaFormCard(
                    form = uiState.form,
                    isSubmitting = uiState.isSubmitting,
                    errorMessage = uiState.errorMessage,
                    onTitleChange = onTitleChange,
                    onDescriptionChange = onDescriptionChange,
                    onCategoryChange = onCategoryChange,
                    onSubmit = onSubmit,
                    onCancel = onCloseForm
                )
            }
        }

        item {
            Text(
                text = stringResource(R.string.operator_ideas_list_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        if (sortedIdeas.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = InnovateSurface)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.Lightbulb,
                            contentDescription = null,
                            tint = InnovatePrimary,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Text(
                            text = stringResource(R.string.operator_ideas_empty),
                            color = InnovateTextSecondary
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = onOpenForm,
                            colors = ButtonDefaults.buttonColors(containerColor = InnovatePrimary)
                        ) {
                            Text(
                                stringResource(R.string.operator_action_new_idea),
                                color = InnovateOnPrimary
                            )
                        }
                    }
                }
            }
        } else {
            items(sortedIdeas, key = { it.id }) { idea ->
                OperatorIdeaListCard(idea = idea)
            }
        }
    }
}
