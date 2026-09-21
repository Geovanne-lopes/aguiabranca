package br.com.fiap.challengeaguiabranca.ui.feature.operator.ideas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import br.com.fiap.challengeaguiabranca.R
import br.com.fiap.challengeaguiabranca.domain.model.Idea
import br.com.fiap.challengeaguiabranca.domain.model.IdeaCategory
import br.com.fiap.challengeaguiabranca.domain.model.IdeaStatus
import br.com.fiap.challengeaguiabranca.domain.model.StrategicGuideline
import br.com.fiap.challengeaguiabranca.ui.theme.InnovateSurface
import br.com.fiap.challengeaguiabranca.ui.theme.InnovateOnPrimary
import br.com.fiap.challengeaguiabranca.ui.theme.InnovatePrimary
import br.com.fiap.challengeaguiabranca.ui.theme.InnovateTextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OperatorIdeaFormCard(
    form: IdeaFormState,
    isSubmitting: Boolean,
    errorMessage: String?,
    formTitleRes: Int = R.string.operator_ideas_form_title,
    guidelines: List<StrategicGuideline> = emptyList(),
    linkGuideline: Boolean = false,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onCategoryChange: (IdeaCategory) -> Unit,
    onGuidelineChange: (String) -> Unit = {},
    onSubmit: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    var categoryExpanded by remember { mutableStateOf(false) }
    var guidelineExpanded by remember { mutableStateOf(false) }
    val selectedGuideline = guidelines.find { it.id == form.guidelineId }
    val titleRes = if (form.editingId == null) formTitleRes else R.string.operator_ideas_form_edit

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = InnovateSurface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = stringResource(titleRes),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            OutlinedTextField(
                value = form.title,
                onValueChange = onTitleChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.operator_ideas_field_title)) },
                singleLine = true
            )

            OutlinedTextField(
                value = form.description,
                onValueChange = onDescriptionChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.operator_ideas_field_description)) },
                minLines = 3
            )

            ExposedDropdownMenuBox(
                expanded = categoryExpanded,
                onExpandedChange = { categoryExpanded = !categoryExpanded }
            ) {
                OutlinedTextField(
                    value = form.category.label,
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                    label = { Text(stringResource(R.string.operator_ideas_field_category)) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) }
                )
                ExposedDropdownMenu(
                    expanded = categoryExpanded,
                    onDismissRequest = { categoryExpanded = false }
                ) {
                    IdeaCategory.entries.forEach { category ->
                        DropdownMenuItem(
                            text = { Text(category.label) },
                            onClick = {
                                onCategoryChange(category)
                                categoryExpanded = false
                            }
                        )
                    }
                }
            }

            if (linkGuideline) {
                if (guidelines.isEmpty()) {
                    Text(
                        text = stringResource(R.string.operator_ideas_no_guideline),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                } else {
                    ExposedDropdownMenuBox(
                        expanded = guidelineExpanded,
                        onExpandedChange = { guidelineExpanded = !guidelineExpanded }
                    ) {
                        OutlinedTextField(
                            value = selectedGuideline?.title.orEmpty(),
                            onValueChange = {},
                            readOnly = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                            label = { Text(stringResource(R.string.operator_ideas_field_guideline)) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = guidelineExpanded) }
                        )
                        ExposedDropdownMenu(
                            expanded = guidelineExpanded,
                            onDismissRequest = { guidelineExpanded = false }
                        ) {
                            guidelines.forEach { guideline ->
                                DropdownMenuItem(
                                    text = { Text(guideline.title) },
                                    onClick = {
                                        onGuidelineChange(guideline.id)
                                        guidelineExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            errorMessage?.let { message ->
                Text(text = message, color = MaterialTheme.colorScheme.error)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onCancel, enabled = !isSubmitting) {
                    Text(stringResource(R.string.operator_ideas_cancel))
                }
                Button(
                    onClick = onSubmit,
                    enabled = !isSubmitting,
                    colors = ButtonDefaults.buttonColors(containerColor = InnovatePrimary)
                ) {
                    if (isSubmitting) {
                        CircularProgressIndicator(
                            color = InnovateOnPrimary,
                            modifier = Modifier.height(20.dp)
                        )
                    } else {
                        Text(
                            stringResource(
                                if (form.editingId == null) {
                                    R.string.operator_ideas_submit
                                } else {
                                    R.string.operator_ideas_save
                                }
                            ),
                            color = InnovateOnPrimary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun OperatorIdeaListCard(
    idea: Idea,
    onEdit: (() -> Unit)? = null,
    onDelete: (() -> Unit)? = null,
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
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
            ) {
                Text(
                    text = idea.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                OperatorIdeaStatusChip(status = idea.status)
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = idea.description,
                style = MaterialTheme.typography.bodySmall,
                color = InnovateTextSecondary,
                maxLines = 4
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = idea.category.label,
                style = MaterialTheme.typography.labelSmall,
                color = InnovatePrimary
            )
            idea.guidelineTitle?.takeIf { it.isNotBlank() }?.let { strategy ->
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.manager_project_strategy, strategy),
                    style = MaterialTheme.typography.labelSmall,
                    color = InnovateTextSecondary
                )
            }
            if (idea.status == IdeaStatus.PENDING && (onEdit != null || onDelete != null)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    if (onEdit != null) {
                        IconButton(onClick = onEdit) {
                            Icon(Icons.Default.Edit, contentDescription = stringResource(R.string.operator_ideas_edit))
                        }
                    }
                    if (onDelete != null) {
                        IconButton(onClick = onDelete) {
                            Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.operator_ideas_delete))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OperatorIdeaStatusChip(status: IdeaStatus) {
    val (label, color) = when (status) {
        IdeaStatus.PENDING -> stringResource(R.string.idea_status_pending) to Color(0xFFF59E0B)
        IdeaStatus.APPROVED -> stringResource(R.string.idea_status_approved) to Color(0xFF10B981)
        IdeaStatus.REJECTED -> stringResource(R.string.idea_status_rejected) to Color(0xFFEF4444)
        IdeaStatus.PRIORITIZED -> stringResource(R.string.idea_status_prioritized) to InnovatePrimary
    }
    Text(
        text = label,
        color = color,
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.SemiBold
    )
}
