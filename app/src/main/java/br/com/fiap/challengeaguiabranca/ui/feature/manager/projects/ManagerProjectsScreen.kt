package br.com.fiap.challengeaguiabranca.ui.feature.manager.projects

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.com.fiap.challengeaguiabranca.R
import br.com.fiap.challengeaguiabranca.domain.model.Idea
import br.com.fiap.challengeaguiabranca.domain.model.Project
import br.com.fiap.challengeaguiabranca.domain.model.ProjectStatus
import br.com.fiap.challengeaguiabranca.ui.theme.InnovateSurface
import br.com.fiap.challengeaguiabranca.ui.theme.InnovateOnPrimary
import br.com.fiap.challengeaguiabranca.ui.theme.InnovatePrimary
import br.com.fiap.challengeaguiabranca.ui.theme.InnovateTextSecondary
import br.com.fiap.challengeaguiabranca.ui.util.formatCurrency
import br.com.fiap.challengeaguiabranca.ui.util.formatDate
import org.koin.androidx.compose.koinViewModel

@Composable
fun ManagerProjectsScreen(
    modifier: Modifier = Modifier,
    viewModel: ManagerProjectsViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }

    LaunchedEffect(uiState.message) {
        uiState.message?.let {
            snackbar.showSnackbar(it)
            viewModel.clearMessage()
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        when {
            uiState.isLoading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = InnovatePrimary)
                }
            }
            uiState.editForm.isVisible -> {
                ProjectEditOverlay(
                    form = uiState.editForm,
                    isSaving = uiState.isSaving,
                    onStatusChange = viewModel::onEditStatusChange,
                    onInvestmentChange = viewModel::onEditInvestmentChange,
                    onProfitChange = viewModel::onEditProfitChange,
                    onProductivityChange = viewModel::onEditProductivityChange,
                    onDeadlineDaysChange = viewModel::onEditDeadlineDaysChange,
                    onSave = viewModel::saveEdit,
                    onCancel = viewModel::closeEdit
                )
            }
            else -> {
                ProjectsList(
                    projects = uiState.projects,
                    onEdit = viewModel::openEdit,
                    onDelete = viewModel::deleteProject
                )
                if (!uiState.createDialogVisible) {
                    FloatingActionButton(
                        onClick = viewModel::openCreateDialog,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(16.dp),
                        containerColor = InnovatePrimary
                    ) {
                        Icon(Icons.Default.Add, null, tint = InnovateOnPrimary)
                    }
                }
            }
        }

        SnackbarHost(hostState = snackbar, modifier = Modifier.align(Alignment.BottomCenter))
    }

    if (uiState.createDialogVisible) {
        CreateProjectDialog(
            ideas = uiState.eligibleIdeas,
            selectedIdeaId = uiState.selectedIdeaId,
            isSaving = uiState.isSaving,
            onSelectIdea = viewModel::selectIdeaForCreate,
            onConfirm = viewModel::createProjectFromSelectedIdea,
            onDismiss = viewModel::closeCreateDialog
        )
    }
}

@Composable
private fun ProjectsList(
    projects: List<Project>,
    onEdit: (Project) -> Unit,
    onDelete: (String) -> Unit
) {
    if (projects.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                stringResource(R.string.manager_projects_empty),
                color = InnovateTextSecondary,
                modifier = Modifier.padding(24.dp)
            )
        }
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                stringResource(R.string.manager_projects_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
        items(projects, key = { it.id }) { project ->
            ProjectCard(project = project, onEdit = { onEdit(project) }, onDelete = { onDelete(project.id) })
        }
        item { Spacer(modifier = Modifier.height(88.dp)) }
    }
}

@Composable
private fun ProjectCard(
    project: Project,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val statusColor = Color(project.status.colorHex)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = InnovateSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = onEdit
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .background(statusColor)
            )
            Column(modifier = Modifier.padding(16.dp).weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(project.title, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.manager_project_delete))
                    }
                }
                Text(project.description, style = MaterialTheme.typography.bodySmall, color = InnovateTextSecondary)
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .background(statusColor, RoundedCornerShape(20.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        project.status.label,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "ROI ${"%.0f".format(project.roiPercent)}%",
                    style = MaterialTheme.typography.labelMedium
                )
                Text(
                    stringResource(
                        R.string.manager_project_financials,
                        formatCurrency(project.investmentAmount),
                        formatCurrency(project.obtainedProfit)
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = InnovateTextSecondary
                )
                Text(
                    stringResource(R.string.manager_project_deadline, formatDate(project.deadlineEpochMillis)),
                    style = MaterialTheme.typography.labelSmall,
                    color = InnovateTextSecondary
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreateProjectDialog(
    ideas: List<Idea>,
    selectedIdeaId: String?,
    isSaving: Boolean,
    onSelectIdea: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val selected = ideas.find { it.id == selectedIdeaId }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.manager_project_create_title)) },
        text = {
            if (ideas.isEmpty()) {
                Text(stringResource(R.string.manager_project_no_eligible_ideas))
            } else {
                ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
                    OutlinedTextField(
                        value = selected?.title ?: "",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(stringResource(R.string.manager_project_select_idea)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) }
                    )
                    ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        ideas.forEach { idea ->
                            DropdownMenuItem(
                                text = { Text(idea.title) },
                                onClick = {
                                    onSelectIdea(idea.id)
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                enabled = !isSaving && ideas.isNotEmpty(),
                colors = ButtonDefaults.buttonColors(containerColor = InnovatePrimary)
            ) {
                Text(if (isSaving) "…" else stringResource(R.string.manager_project_create_confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.operator_ideas_cancel))
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProjectEditOverlay(
    form: ProjectEditForm,
    isSaving: Boolean,
    onStatusChange: (ProjectStatus) -> Unit,
    onInvestmentChange: (String) -> Unit,
    onProfitChange: (String) -> Unit,
    onProductivityChange: (String) -> Unit,
    onDeadlineDaysChange: (String) -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit
) {
    var statusExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            stringResource(R.string.manager_project_edit_title),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(form.title, fontWeight = FontWeight.SemiBold)
        Text(form.description, color = InnovateTextSecondary, style = MaterialTheme.typography.bodySmall)
        Spacer(modifier = Modifier.height(16.dp))

        ExposedDropdownMenuBox(expanded = statusExpanded, onExpandedChange = { statusExpanded = it }) {
            OutlinedTextField(
                value = form.status.label,
                onValueChange = {},
                readOnly = true,
                label = { Text(stringResource(R.string.manager_project_field_status)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(statusExpanded) }
            )
            ExposedDropdownMenu(expanded = statusExpanded, onDismissRequest = { statusExpanded = false }) {
                ProjectStatus.entries.forEach { status ->
                    DropdownMenuItem(
                        text = { Text(status.label) },
                        onClick = {
                            onStatusChange(status)
                            statusExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = form.investmentText,
            onValueChange = onInvestmentChange,
            label = { Text(stringResource(R.string.manager_project_field_investment)) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = form.profitText,
            onValueChange = onProfitChange,
            label = { Text(stringResource(R.string.manager_project_field_profit)) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = form.productivityText,
            onValueChange = onProductivityChange,
            label = { Text(stringResource(R.string.manager_project_field_productivity)) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = form.deadlineDaysText,
            onValueChange = onDeadlineDaysChange,
            label = { Text(stringResource(R.string.manager_project_field_deadline_days)) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(24.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            TextButton(onClick = onCancel, modifier = Modifier.weight(1f)) {
                Text(stringResource(R.string.operator_ideas_cancel))
            }
            Button(
                onClick = onSave,
                enabled = !isSaving,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = InnovatePrimary)
            ) {
                Text(stringResource(R.string.manager_project_save))
            }
        }
        Spacer(modifier = Modifier.height(80.dp))
    }
}
