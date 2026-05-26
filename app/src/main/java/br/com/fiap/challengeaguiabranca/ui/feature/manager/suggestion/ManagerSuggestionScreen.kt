package br.com.fiap.challengeaguiabranca.ui.feature.manager.suggestion

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.com.fiap.challengeaguiabranca.R
import br.com.fiap.challengeaguiabranca.ui.theme.InnovateBackground
import br.com.fiap.challengeaguiabranca.ui.theme.InnovateOnPrimary
import br.com.fiap.challengeaguiabranca.ui.theme.InnovatePrimary
import br.com.fiap.challengeaguiabranca.ui.theme.InnovateTextPrimary
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManagerSuggestionScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ManagerSuggestionViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }
    var expanded by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.successMessage) {
        uiState.successMessage?.let {
            snackbar.showSnackbar(it)
            viewModel.clearSuccessMessage()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = InnovateBackground,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.manager_suggestion_title), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    androidx.compose.material3.IconButton(onClick = onBack) {
                        androidx.compose.material3.Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(stringResource(R.string.manager_suggestion_subtitle))

            val selected = uiState.operators.find { it.id == uiState.selectedOperatorId }
            ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                OutlinedTextField(
                    value = selected?.name ?: "",
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                    label = { Text(stringResource(R.string.manager_suggestion_target)) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) }
                )
                ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    uiState.operators.forEach { op ->
                        DropdownMenuItem(
                            text = { Text("${op.name} (${op.email})") },
                            onClick = {
                                viewModel.onOperatorSelected(op.id)
                                expanded = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = uiState.message,
                onValueChange = viewModel::onMessageChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.manager_suggestion_message)) },
                minLines = 4
            )

            uiState.errorMessage?.let { Text(it, color = androidx.compose.material3.MaterialTheme.colorScheme.error) }

            Button(
                onClick = viewModel::sendSuggestion,
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isSending,
                colors = ButtonDefaults.buttonColors(containerColor = InnovatePrimary)
            ) {
                if (uiState.isSending) {
                    CircularProgressIndicator(color = InnovateOnPrimary, modifier = Modifier.height(22.dp))
                } else {
                    Text(stringResource(R.string.manager_suggestion_send), color = InnovateOnPrimary)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
