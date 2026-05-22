package br.com.fiap.challengeaguiabranca.ui.feature.operator.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.TrackChanges
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import br.com.fiap.challengeaguiabranca.R
import br.com.fiap.challengeaguiabranca.ui.feature.operator.home.OperatorTab
import br.com.fiap.challengeaguiabranca.ui.theme.InnovatePrimary
import br.com.fiap.challengeaguiabranca.ui.theme.InnovateTextSecondary

@Composable
fun OperatorBottomBar(
    selectedTab: OperatorTab,
    onTabSelected: (OperatorTab) -> Unit
) {
    NavigationBar {
        NavigationBarItem(
            selected = selectedTab == OperatorTab.HOME,
            onClick = { onTabSelected(OperatorTab.HOME) },
            icon = { Icon(Icons.Default.Home, contentDescription = null) },
            label = { Text(stringResource(R.string.operator_nav_home)) },
            colors = navItemColors()
        )
        NavigationBarItem(
            selected = selectedTab == OperatorTab.STRATEGIES,
            onClick = { onTabSelected(OperatorTab.STRATEGIES) },
            icon = { Icon(Icons.Outlined.TrackChanges, contentDescription = null) },
            label = { Text(stringResource(R.string.operator_nav_strategies)) },
            colors = navItemColors()
        )
        NavigationBarItem(
            selected = selectedTab == OperatorTab.IDEAS,
            onClick = { onTabSelected(OperatorTab.IDEAS) },
            icon = { Icon(Icons.Default.Lightbulb, contentDescription = null) },
            label = { Text(stringResource(R.string.operator_nav_ideas)) },
            colors = navItemColors()
        )
        NavigationBarItem(
            selected = selectedTab == OperatorTab.PROFILE,
            onClick = { onTabSelected(OperatorTab.PROFILE) },
            icon = { Icon(Icons.Default.Person, contentDescription = null) },
            label = { Text(stringResource(R.string.operator_nav_profile)) },
            colors = navItemColors()
        )
    }
}

@Composable
private fun navItemColors() = NavigationBarItemDefaults.colors(
    selectedIconColor = InnovatePrimary,
    selectedTextColor = InnovatePrimary,
    indicatorColor = InnovatePrimary.copy(alpha = 0.12f),
    unselectedIconColor = InnovateTextSecondary,
    unselectedTextColor = InnovateTextSecondary
)
