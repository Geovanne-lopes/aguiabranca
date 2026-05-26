package br.com.fiap.challengeaguiabranca.ui.feature.manager.guidelines

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.com.fiap.challengeaguiabranca.R
import br.com.fiap.challengeaguiabranca.ui.feature.operator.components.GuidelineReadOnlyCard
import br.com.fiap.challengeaguiabranca.ui.theme.InnovatePrimary
import br.com.fiap.challengeaguiabranca.ui.theme.InnovateTextSecondary
import br.com.fiap.challengeaguiabranca.ui.util.premiumListEntrance
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import org.koin.androidx.compose.koinViewModel

@Composable
fun ManagerGuidelinesScreen(
    modifier: Modifier = Modifier,
    viewModel: ManagerGuidelinesViewModel = koinViewModel()
) {
    val guidelines by viewModel.guidelines.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    if (isLoading) {
        Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = InnovatePrimary)
        }
        return
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.manager_guidelines_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
        if (guidelines.isEmpty()) {
            item {
                Text(stringResource(R.string.manager_guidelines_empty), color = InnovateTextSecondary)
            }
        } else {
            itemsIndexed(guidelines, key = { _, guideline -> guideline.id }) { index, guideline ->
                GuidelineReadOnlyCard(
                    guideline = guideline,
                    modifier = Modifier.premiumListEntrance(index)
                )
            }
        }
        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}
