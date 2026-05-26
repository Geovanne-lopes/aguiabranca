package br.com.fiap.challengeaguiabranca.ui.feature.manager.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Work
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import br.com.fiap.challengeaguiabranca.R
import br.com.fiap.challengeaguiabranca.ui.feature.manager.ManagerTab
import br.com.fiap.challengeaguiabranca.ui.theme.InnovatePrimary
import br.com.fiap.challengeaguiabranca.ui.theme.InnovateTextSecondary
import br.com.fiap.challengeaguiabranca.ui.util.NavAuraIcon

@Composable
fun ManagerBottomBar(
    selectedTab: ManagerTab,
    onTabSelected: (ManagerTab) -> Unit
) {
    NavigationBar {
        NavigationBarItem(
            selected = selectedTab == ManagerTab.HOME,
            onClick = { onTabSelected(ManagerTab.HOME) },
            icon = {
                NavAuraIcon(selectedTab == ManagerTab.HOME, InnovatePrimary) {
                    Icon(Icons.Default.Home, null)
                }
            },
            label = { Text(stringResource(R.string.manager_nav_home)) },
            colors = navColors()
        )
        NavigationBarItem(
            selected = selectedTab == ManagerTab.CURATION,
            onClick = { onTabSelected(ManagerTab.CURATION) },
            icon = {
                NavAuraIcon(selectedTab == ManagerTab.CURATION, InnovatePrimary) {
                    Icon(Icons.Default.Lightbulb, null)
                }
            },
            label = { Text(stringResource(R.string.manager_nav_curation)) },
            colors = navColors()
        )
        NavigationBarItem(
            selected = selectedTab == ManagerTab.PROJECTS,
            onClick = { onTabSelected(ManagerTab.PROJECTS) },
            icon = {
                NavAuraIcon(selectedTab == ManagerTab.PROJECTS, InnovatePrimary) {
                    Icon(Icons.Default.Work, null)
                }
            },
            label = { Text(stringResource(R.string.manager_nav_projects)) },
            colors = navColors()
        )
        NavigationBarItem(
            selected = selectedTab == ManagerTab.GUIDELINES,
            onClick = { onTabSelected(ManagerTab.GUIDELINES) },
            icon = {
                NavAuraIcon(selectedTab == ManagerTab.GUIDELINES, InnovatePrimary) {
                    Icon(Icons.Outlined.MenuBook, null)
                }
            },
            label = { Text(stringResource(R.string.manager_nav_guidelines)) },
            colors = navColors()
        )
        NavigationBarItem(
            selected = selectedTab == ManagerTab.PROFILE,
            onClick = { onTabSelected(ManagerTab.PROFILE) },
            icon = {
                NavAuraIcon(selectedTab == ManagerTab.PROFILE, InnovatePrimary) {
                    Icon(Icons.Default.Person, null)
                }
            },
            label = { Text(stringResource(R.string.operator_nav_profile)) },
            colors = navColors()
        )
    }
}

@Composable
private fun navColors() = NavigationBarItemDefaults.colors(
    selectedIconColor = InnovatePrimary,
    selectedTextColor = InnovatePrimary,
    indicatorColor = InnovatePrimary.copy(alpha = 0.12f),
    unselectedIconColor = InnovateTextSecondary,
    unselectedTextColor = InnovateTextSecondary
)
