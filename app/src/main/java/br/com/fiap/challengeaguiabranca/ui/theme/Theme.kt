package br.com.fiap.challengeaguiabranca.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val InnovateColorScheme = lightColorScheme(
    primary = InnovatePrimary,
    onPrimary = InnovateOnPrimary,
    primaryContainer = InnovatePrimaryLight,
    onPrimaryContainer = InnovateTextPrimary,
    secondary = InnovateManagerPink,
    background = InnovateBackground,
    onBackground = InnovateTextPrimary,
    surface = InnovateSurface,
    onSurface = InnovateTextPrimary,
    surfaceVariant = InnovateBackground,
    onSurfaceVariant = InnovateTextSecondary,
    error = InnovateError
)

@Composable
fun ChallengeAguiaBrancaTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = InnovateColorScheme,
        typography = Typography,
        content = content
    )
}
