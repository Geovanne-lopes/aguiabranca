package br.com.fiap.challengeaguiabranca.ui.feature.operator.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import br.com.fiap.challengeaguiabranca.domain.model.Idea
import br.com.fiap.challengeaguiabranca.ui.feature.operator.ideas.OperatorIdeaListCard
import br.com.fiap.challengeaguiabranca.ui.theme.InnovateBackground
import br.com.fiap.challengeaguiabranca.ui.theme.InnovateTextPrimary
import br.com.fiap.challengeaguiabranca.ui.theme.InnovateTextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OperatorAllIdeasScreen(
    ideas: List<Idea>,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = InnovateBackground,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(R.string.operator_all_ideas_title),
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
        if (ideas.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(24.dp)
            ) {
                Text(
                    text = stringResource(R.string.operator_no_ideas),
                    color = InnovateTextSecondary,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item { Spacer(modifier = Modifier.height(4.dp)) }
                items(ideas, key = { it.id }) { idea ->
                    OperatorIdeaListCard(idea = idea)
                }
                item { Spacer(modifier = Modifier.height(16.dp)) }
            }
        }
    }
}
