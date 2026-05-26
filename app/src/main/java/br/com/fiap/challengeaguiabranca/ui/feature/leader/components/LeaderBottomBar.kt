package br.com.fiap.challengeaguiabranca.ui.feature.leader.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import br.com.fiap.challengeaguiabranca.R
import br.com.fiap.challengeaguiabranca.ui.feature.leader.LeaderTab
import br.com.fiap.challengeaguiabranca.ui.theme.InnovateLeaderPurple
import br.com.fiap.challengeaguiabranca.ui.theme.InnovateTextSecondary
import br.com.fiap.challengeaguiabranca.ui.util.NavAuraIcon

@Composable
fun LeaderBottomBar(
    selectedTab: LeaderTab,
    onTabSelected: (LeaderTab) -> Unit
) {
    NavigationBar {
        NavigationBarItem(
            selected = selectedTab == LeaderTab.HOME,
            onClick = { onTabSelected(LeaderTab.HOME) },
            icon = {
                NavAuraIcon(selectedTab == LeaderTab.HOME, InnovateLeaderPurple) {
                    Icon(Icons.Default.Dashboard, null)
                }
            },
            label = { Text(stringResource(R.string.leader_nav_home)) },
            colors = navColors()
        )
        NavigationBarItem(
            selected = selectedTab == LeaderTab.GUIDELINES,
            onClick = { onTabSelected(LeaderTab.GUIDELINES) },
            icon = {
                NavAuraIcon(selectedTab == LeaderTab.GUIDELINES, InnovateLeaderPurple) {
                    Icon(Icons.Outlined.MenuBook, null)
                }
            },
            label = { Text(stringResource(R.string.leader_nav_guidelines)) },
            colors = navColors()
        )
        NavigationBarItem(
            selected = selectedTab == LeaderTab.TEAM,
            onClick = { onTabSelected(LeaderTab.TEAM) },
            icon = {
                NavAuraIcon(selectedTab == LeaderTab.TEAM, InnovateLeaderPurple) {
                    Icon(Icons.Default.Groups, null)
                }
            },
            label = { Text(stringResource(R.string.leader_nav_team)) },
            colors = navColors()
        )
        NavigationBarItem(
            selected = selectedTab == LeaderTab.TRACKING,
            onClick = { onTabSelected(LeaderTab.TRACKING) },
            icon = {
                NavAuraIcon(selectedTab == LeaderTab.TRACKING, InnovateLeaderPurple) {
                    Icon(Icons.Default.TrackChanges, null)
                }
            },
            label = { Text(stringResource(R.string.leader_nav_tracking)) },
            colors = navColors()
        )
        NavigationBarItem(
            selected = selectedTab == LeaderTab.PROFILE,
            onClick = { onTabSelected(LeaderTab.PROFILE) },
            icon = {
                NavAuraIcon(selectedTab == LeaderTab.PROFILE, InnovateLeaderPurple) {
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
    selectedIconColor = InnovateLeaderPurple,
    selectedTextColor = InnovateLeaderPurple,
    indicatorColor = InnovateLeaderPurple.copy(alpha = 0.12f),
    unselectedIconColor = InnovateTextSecondary,
    unselectedTextColor = InnovateTextSecondary
)
