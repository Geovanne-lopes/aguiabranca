package br.com.fiap.challengeaguiabranca.ui.feature.leader.guidelines

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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import br.com.fiap.challengeaguiabranca.domain.model.GuidelineHistoryEntry
import br.com.fiap.challengeaguiabranca.domain.model.StrategicGuideline
import br.com.fiap.challengeaguiabranca.ui.util.formatDate
import br.com.fiap.challengeaguiabranca.ui.theme.InnovateSurface
import br.com.fiap.challengeaguiabranca.ui.theme.InnovateOnPrimary
import br.com.fiap.challengeaguiabranca.ui.theme.InnovatePrimary
import br.com.fiap.challengeaguiabranca.ui.theme.InnovateTextSecondary
import br.com.fiap.challengeaguiabranca.ui.util.premiumListEntrance
import org.koin.androidx.compose.koinViewModel

@Composable
fun LeaderGuidelinesScreen(
    modifier: Modifier = Modifier,
    viewModel: LeaderGuidelinesViewModel = koinViewModel()
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
            uiState.form.isVisible -> {
                GuidelineFormOverlay(
                    form = uiState.form,
                    isSaving = uiState.isSaving,
                    onTitleChange = viewModel::onTitleChange,
                    onContentChange = viewModel::onContentChange,
                    onCategoryChange = viewModel::onCategoryChange,
                    onCampaignChange = viewModel::onCampaignChange,
                    onSave = viewModel::saveForm,
                    onCancel = viewModel::closeForm
                )
            }
            uiState.history.isVisible -> {
                GuidelineHistoryOverlay(
                    history = uiState.history,
                    onClose = viewModel::closeHistory
                )
            }
            else -> {
                GuidelinesList(
                    guidelines = uiState.guidelines,
                    onEdit = viewModel::openEditForm,
                    onDelete = viewModel::deleteGuideline,
                    onHistory = viewModel::openHistory
                )
                FloatingActionButton(
                    onClick = viewModel::openCreateForm,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp),
                    containerColor = InnovatePrimary
                ) {
                    Icon(Icons.Default.Add, null, tint = InnovateOnPrimary)
                }
            }
        }
        SnackbarHost(hostState = snackbar, modifier = Modifier.align(Alignment.BottomCenter))
    }
}

@Composable
private fun GuidelinesList(
    guidelines: List<StrategicGuideline>,
    onEdit: (StrategicGuideline) -> Unit,
    onDelete: (String) -> Unit,
    onHistory: (StrategicGuideline) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                stringResource(R.string.leader_guidelines_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
        if (guidelines.isEmpty()) {
            item {
                Text(stringResource(R.string.leader_guidelines_empty), color = InnovateTextSecondary)
            }
        } else {
            itemsIndexed(guidelines, key = { _, guideline -> guideline.id }) { index, guideline ->
                GuidelineManageCard(
                    guideline = guideline,
                    onEdit = { onEdit(guideline) },
                    onDelete = { onDelete(guideline.id) },
                    onHistory = { onHistory(guideline) },
                    modifier = Modifier.premiumListEntrance(index)
                )
            }
        }
        item { Spacer(modifier = Modifier.height(88.dp)) }
    }
}

@Composable
private fun GuidelineManageCard(
    guideline: StrategicGuideline,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onHistory: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = InnovateSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(guideline.title, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = stringResource(R.string.leader_guideline_edit))
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.leader_guideline_delete))
                }
            }
            Text(guideline.content, style = MaterialTheme.typography.bodySmall, color = InnovateTextSecondary)
            GuidelineMetaLine(guideline)
            Spacer(modifier = Modifier.height(8.dp))
            TextButton(onClick = onHistory) {
                Text(stringResource(R.string.leader_guideline_history))
            }
        }
    }
}

@Composable
private fun GuidelineFormOverlay(
    form: GuidelineFormState,
    isSaving: Boolean,
    onTitleChange: (String) -> Unit,
    onContentChange: (String) -> Unit,
    onCategoryChange: (String) -> Unit,
    onCampaignChange: (String) -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit
) {
    val titleRes = if (form.editingId == null) {
        R.string.leader_guideline_form_create
    } else {
        R.string.leader_guideline_form_edit
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            stringResource(titleRes),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = form.title,
            onValueChange = onTitleChange,
            label = { Text(stringResource(R.string.leader_guideline_field_title)) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = form.content,
            onValueChange = onContentChange,
            label = { Text(stringResource(R.string.leader_guideline_field_content)) },
            modifier = Modifier.fillMaxWidth(),
            minLines = 5
        )
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = form.category,
            onValueChange = onCategoryChange,
            label = { Text(stringResource(R.string.leader_guideline_field_category)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = form.campaign,
            onValueChange = onCampaignChange,
            label = { Text(stringResource(R.string.leader_guideline_field_campaign)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
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
                Text(stringResource(R.string.leader_guideline_save))
            }
        }
        Spacer(modifier = Modifier.height(80.dp))
    }
}

@Composable
private fun GuidelineHistoryOverlay(
    history: GuidelineHistoryState,
    onClose: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            stringResource(R.string.leader_guideline_history_title, history.title),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))
        when {
            history.isLoading -> CircularProgressIndicator(color = InnovatePrimary)
            history.items.isEmpty() -> Text(
                stringResource(R.string.leader_guideline_history_empty),
                color = InnovateTextSecondary
            )
            else -> history.items.forEach { entry ->
                HistoryRow(entry)
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        TextButton(onClick = onClose) {
            Text(stringResource(R.string.leader_guideline_history_close))
        }
    }
}

@Composable
private fun HistoryRow(entry: GuidelineHistoryEntry) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = InnovateSurface)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(historyActionLabel(entry.action), fontWeight = FontWeight.Bold)
            Text(
                formatDate(entry.occurredAtEpochMillis),
                style = MaterialTheme.typography.labelSmall,
                color = InnovateTextSecondary
            )
            Text(
                stringResource(R.string.leader_guideline_history_id, entry.id),
                style = MaterialTheme.typography.labelSmall,
                color = InnovateTextSecondary
            )
            GuidelineMetaText(entry.category, entry.campaign)
        }
    }
}

@Composable
private fun GuidelineMetaLine(guideline: StrategicGuideline) {
    Spacer(modifier = Modifier.height(8.dp))
    GuidelineMetaText(guideline.category, guideline.campaign)
}

@Composable
private fun GuidelineMetaText(category: String?, campaign: String?) {
    Text(
        stringResource(
            R.string.guideline_meta,
            category?.takeIf { it.isNotBlank() } ?: "—",
            campaign?.takeIf { it.isNotBlank() } ?: "—"
        ),
        style = MaterialTheme.typography.labelMedium,
        color = InnovatePrimary
    )
}

@Composable
private fun historyActionLabel(action: String): String = when (action) {
    "CREATED" -> stringResource(R.string.guideline_history_created)
    "UPDATED" -> stringResource(R.string.guideline_history_updated)
    "DELETED" -> stringResource(R.string.guideline_history_deleted)
    else -> action
}
