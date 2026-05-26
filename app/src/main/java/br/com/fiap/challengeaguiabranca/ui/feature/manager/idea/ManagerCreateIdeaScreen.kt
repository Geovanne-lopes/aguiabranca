package br.com.fiap.challengeaguiabranca.ui.feature.manager.idea

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.com.fiap.challengeaguiabranca.R
import br.com.fiap.challengeaguiabranca.ui.feature.operator.ideas.IdeaFormState
import br.com.fiap.challengeaguiabranca.ui.feature.operator.ideas.OperatorIdeaFormCard
import br.com.fiap.challengeaguiabranca.ui.theme.InnovateBackground
import br.com.fiap.challengeaguiabranca.ui.theme.InnovateTextPrimary
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManagerCreateIdeaScreen(
    onBack: () -> Unit,
    onSuccess: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ManagerCreateIdeaViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }

    LaunchedEffect(uiState.successMessage) {
        uiState.successMessage?.let {
            snackbar.showSnackbar(it)
            viewModel.clearSuccessMessage()
            onSuccess()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = InnovateBackground,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.manager_create_idea_title), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = InnovateBackground,
                    titleContentColor = InnovateTextPrimary
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbar) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            item { Spacer(modifier = Modifier.height(8.dp)) }
            item {
                OperatorIdeaFormCard(
                    form = IdeaFormState(
                        title = uiState.title,
                        description = uiState.description,
                        category = uiState.category,
                        isVisible = true
                    ),
                    isSubmitting = uiState.isSubmitting,
                    errorMessage = uiState.errorMessage,
                    formTitleRes = R.string.manager_create_idea_title,
                    onTitleChange = viewModel::onTitleChange,
                    onDescriptionChange = viewModel::onDescriptionChange,
                    onCategoryChange = viewModel::onCategoryChange,
                    onSubmit = viewModel::submit,
                    onCancel = onBack
                )
            }
        }
    }
}
