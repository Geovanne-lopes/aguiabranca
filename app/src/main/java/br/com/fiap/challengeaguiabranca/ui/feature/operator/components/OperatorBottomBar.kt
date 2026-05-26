package br.com.fiap.challengeaguiabranca.ui.feature.operator.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.TrackChanges
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import br.com.fiap.challengeaguiabranca.R
import br.com.fiap.challengeaguiabranca.ui.feature.operator.home.OperatorTab
import br.com.fiap.challengeaguiabranca.ui.theme.currentRoleAccent
import br.com.fiap.challengeaguiabranca.ui.theme.innovateBorderColor
import br.com.fiap.challengeaguiabranca.ui.theme.innovateSurfaceColor
import br.com.fiap.challengeaguiabranca.ui.theme.InnovateTextSecondary
import br.com.fiap.challengeaguiabranca.ui.util.NavAuraIcon

@Composable
fun OperatorBottomBar(
    selectedTab: OperatorTab,
    onTabSelected: (OperatorTab) -> Unit
) {
    val accent = currentRoleAccent().accent
    NavigationBar(
        containerColor = innovateSurfaceColor(),
        tonalElevation = 0.dp,
        windowInsets = NavigationBarDefaults.windowInsets
    ) {
        NavigationBarItem(
            selected = selectedTab == OperatorTab.HOME,
            onClick = { onTabSelected(OperatorTab.HOME) },
            icon = {
                NavAuraIcon(selectedTab == OperatorTab.HOME, accent) {
                    Icon(Icons.Default.Home, contentDescription = null)
                }
            },
            label = { Text(stringResource(R.string.operator_nav_home)) },
            colors = navItemColors()
        )
        NavigationBarItem(
            selected = selectedTab == OperatorTab.STRATEGIES,
            onClick = { onTabSelected(OperatorTab.STRATEGIES) },
            icon = {
                NavAuraIcon(selectedTab == OperatorTab.STRATEGIES, accent) {
                    Icon(Icons.Outlined.TrackChanges, contentDescription = null)
                }
            },
            label = { Text(stringResource(R.string.operator_nav_strategies)) },
            colors = navItemColors()
        )
        NavigationBarItem(
            selected = selectedTab == OperatorTab.IDEAS,
            onClick = { onTabSelected(OperatorTab.IDEAS) },
            icon = {
                NavAuraIcon(selectedTab == OperatorTab.IDEAS, accent) {
                    Icon(Icons.Default.Lightbulb, contentDescription = null)
                }
            },
            label = { Text(stringResource(R.string.operator_nav_ideas)) },
            colors = navItemColors()
        )
        NavigationBarItem(
            selected = selectedTab == OperatorTab.PROFILE,
            onClick = { onTabSelected(OperatorTab.PROFILE) },
            icon = {
                NavAuraIcon(selectedTab == OperatorTab.PROFILE, accent) {
                    Icon(Icons.Default.Person, contentDescription = null)
                }
            },
            label = { Text(stringResource(R.string.operator_nav_profile)) },
            colors = navItemColors()
        )
    }
}

@Composable
private fun navItemColors() = NavigationBarItemDefaults.colors(
    selectedIconColor = currentRoleAccent().accent,
    selectedTextColor = currentRoleAccent().accent,
    indicatorColor = currentRoleAccent().accentSoft,
    unselectedIconColor = InnovateTextSecondary,
    unselectedTextColor = InnovateTextSecondary
)
