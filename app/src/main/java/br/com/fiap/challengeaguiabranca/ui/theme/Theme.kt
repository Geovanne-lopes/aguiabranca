package br.com.fiap.challengeaguiabranca.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color

val LocalInnovateDarkTheme = compositionLocalOf { false }

private fun lightColorScheme(accent: RoleAccent) = lightColorScheme(
    primary = accent.accent,
    onPrimary = InnovateOnPrimary,
    primaryContainer = InnovatePrimaryLight,
    onPrimaryContainer = InnovateTextPrimaryLight,
    secondary = InnovateManagerPink,
    background = InnovateBackgroundLight,
    onBackground = InnovateTextPrimaryLight,
    surface = InnovateSurfaceLight,
    onSurface = InnovateTextPrimaryLight,
    surfaceVariant = InnovateSurface2Light,
    onSurfaceVariant = InnovateTextSecondaryLight,
    error = InnovateError,
    outline = InnovateBorderLight
)

private fun darkColorScheme(accent: RoleAccent) = darkColorScheme(
    primary = accent.accent,
    onPrimary = InnovateOnPrimary,
    primaryContainer = InnovatePrimaryDark,
    onPrimaryContainer = InnovateTextPrimaryDark,
    secondary = InnovateManagerPink,
    background = InnovateBackgroundDark,
    onBackground = InnovateTextPrimaryDark,
    surface = InnovateSurfaceDark,
    onSurface = InnovateTextPrimaryDark,
    surfaceVariant = InnovateSurface2Dark,
    onSurfaceVariant = InnovateTextSecondaryDark,
    error = InnovateError,
    outline = InnovateBorderDark
)

@Composable
fun RoleThemedScreen(
    role: br.com.fiap.challengeaguiabranca.domain.model.UserRole,
    content: @Composable () -> Unit
) {
    val darkTheme = LocalInnovateDarkTheme.current
    ChallengeAguiaBrancaTheme(darkTheme = darkTheme, role = role, content = content)
}

@Composable
fun ChallengeAguiaBrancaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    role: br.com.fiap.challengeaguiabranca.domain.model.UserRole? = null,
    content: @Composable () -> Unit
) {
    val accent = RoleAccent.forRole(role)
    val scheme = if (darkTheme) darkColorScheme(accent) else lightColorScheme(accent)
    CompositionLocalProvider(
        LocalInnovateDarkTheme provides darkTheme,
        LocalRoleAccent provides accent
    ) {
        MaterialTheme(
            colorScheme = scheme,
            typography = Typography,
            content = content
        )
    }
}
