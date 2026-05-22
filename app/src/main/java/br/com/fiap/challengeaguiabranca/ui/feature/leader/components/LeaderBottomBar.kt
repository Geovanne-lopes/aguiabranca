package br.com.fiap.challengeaguiabranca.ui.feature.leader.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
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
import br.com.fiap.challengeaguiabranca.ui.theme.InnovatePrimary
import br.com.fiap.challengeaguiabranca.ui.theme.InnovateTextSecondary

@Composable
fun LeaderBottomBar(
    selectedTab: LeaderTab,
    onTabSelected: (LeaderTab) -> Unit
) {
    NavigationBar {
        NavigationBarItem(
            selected = selectedTab == LeaderTab.HOME,
            onClick = { onTabSelected(LeaderTab.HOME) },
            icon = { Icon(Icons.Default.Home, null) },
            label = { Text(stringResource(R.string.leader_nav_home)) },
            colors = navColors()
        )
        NavigationBarItem(
            selected = selectedTab == LeaderTab.GUIDELINES,
            onClick = { onTabSelected(LeaderTab.GUIDELINES) },
            icon = { Icon(Icons.Outlined.MenuBook, null) },
            label = { Text(stringResource(R.string.leader_nav_guidelines)) },
            colors = navColors()
        )
        NavigationBarItem(
            selected = selectedTab == LeaderTab.TRACKING,
            onClick = { onTabSelected(LeaderTab.TRACKING) },
            icon = { Icon(Icons.Default.TrackChanges, null) },
            label = { Text(stringResource(R.string.leader_nav_tracking)) },
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
