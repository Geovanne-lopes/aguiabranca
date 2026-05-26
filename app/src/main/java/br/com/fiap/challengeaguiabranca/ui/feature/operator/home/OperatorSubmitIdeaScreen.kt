package br.com.fiap.challengeaguiabranca.ui.feature.operator.home

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
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import br.com.fiap.challengeaguiabranca.R
import br.com.fiap.challengeaguiabranca.domain.model.IdeaCategory
import br.com.fiap.challengeaguiabranca.ui.feature.operator.ideas.IdeaFormState
import br.com.fiap.challengeaguiabranca.ui.feature.operator.ideas.OperatorIdeaFormCard
import br.com.fiap.challengeaguiabranca.ui.theme.InnovateBackground
import br.com.fiap.challengeaguiabranca.ui.theme.InnovateTextPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OperatorSubmitIdeaScreen(
    category: IdeaCategory,
    form: IdeaFormState,
    isSubmitting: Boolean,
    errorMessage: String?,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onCategoryChange: (IdeaCategory) -> Unit,
    onSubmit: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val titleRes = when (category) {
        IdeaCategory.OTHER -> R.string.operator_report_form_title
        else -> R.string.operator_ideas_form_title
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = InnovateBackground,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(titleRes),
                        fontWeight = FontWeight.Bold
                    )
                },
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
        }
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
                    form = form,
                    isSubmitting = isSubmitting,
                    errorMessage = errorMessage,
                    formTitleRes = titleRes,
                    onTitleChange = onTitleChange,
                    onDescriptionChange = onDescriptionChange,
                    onCategoryChange = onCategoryChange,
                    onSubmit = onSubmit,
                    onCancel = onBack
                )
            }
            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }
}
