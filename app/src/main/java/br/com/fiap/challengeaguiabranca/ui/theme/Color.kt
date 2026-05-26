package br.com.fiap.challengeaguiabranca.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color

// Paleta InnovateCorp — alinhada ao web-preview/src/theme.css
val InnovatePrimary = Color(0xFF6D5DFC)
val InnovatePrimaryDark = Color(0xFF5746D8)
val InnovatePrimaryLight = Color(0xFFA29BFE)

val InnovateBackgroundLight = Color(0xFFF7F5F0)
val InnovateBackgroundDark = Color(0xFF11110F)
val InnovateSurfaceLight = Color(0xFFFFFFFF)
val InnovateSurfaceDark = Color(0xFF1B1A17)
val InnovateSurface2Light = Color(0xFFF2EFE7)
val InnovateSurface2Dark = Color(0xFF24221F)
val InnovateOnPrimary = Color(0xFFFFFFFF)
val InnovateTextPrimaryLight = Color(0xFF1F1F1D)
val InnovateTextPrimaryDark = Color(0xFFF6F1E8)
val InnovateTextSecondaryLight = Color(0xFF6F6B63)
val InnovateTextSecondaryDark = Color(0xFFB9B1A4)
val InnovateTextTertiaryLight = Color(0xFF9B958A)
val InnovateTextTertiaryDark = Color(0xFF8A8378)
val InnovateLinkLight = Color(0xFF4F46E5)
val InnovateLinkDark = Color(0xFFA5B4FC)
val InnovateBorderLight = Color(0xFFE3DED4)
val InnovateBorderDark = Color(0xFF34312B)

val InnovateOperatorBlue = Color(0xFF3B82F6)
val InnovateOperatorBlueDark = Color(0xFF1D4ED8)
val InnovateManagerPink = Color(0xFFEC4899)
val InnovateManagerPinkDark = Color(0xFFB91C4D)
val InnovateLeaderPurple = Color(0xFF7C3AED)
val InnovateLeaderPurpleDark = Color(0xFF4F1D96)
val InnovateSuccess = Color(0xFF10B981)
val InnovateWarning = Color(0xFFF59E0B)
val InnovateError = Color(0xFFEF4444)
val InnovateLogoBackground = Color(0xFFFFE8D5)
val InnovateLogoForeground = Color(0xFF9A3412)

// Aliases theme-aware: resolvem para o valor claro/escuro de acordo
// com LocalInnovateDarkTheme. Mantém o nome usado em toda a base sem
// quebrar call sites.
val InnovateBackground: Color
    @Composable
    @ReadOnlyComposable
    get() = if (LocalInnovateDarkTheme.current) InnovateBackgroundDark else InnovateBackgroundLight

val InnovateSurface: Color
    @Composable
    @ReadOnlyComposable
    get() = if (LocalInnovateDarkTheme.current) InnovateSurfaceDark else InnovateSurfaceLight

val InnovateSurface2: Color
    @Composable
    @ReadOnlyComposable
    get() = if (LocalInnovateDarkTheme.current) InnovateSurface2Dark else InnovateSurface2Light

val InnovateTextPrimary: Color
    @Composable
    @ReadOnlyComposable
    get() = if (LocalInnovateDarkTheme.current) InnovateTextPrimaryDark else InnovateTextPrimaryLight

val InnovateTextSecondary: Color
    @Composable
    @ReadOnlyComposable
    get() = if (LocalInnovateDarkTheme.current) InnovateTextSecondaryDark else InnovateTextSecondaryLight

val InnovateTextTertiary: Color
    @Composable
    @ReadOnlyComposable
    get() = if (LocalInnovateDarkTheme.current) InnovateTextTertiaryDark else InnovateTextTertiaryLight

val InnovateLink: Color
    @Composable
    @ReadOnlyComposable
    get() = if (LocalInnovateDarkTheme.current) InnovateLinkDark else InnovateLinkLight

val InnovateBorder: Color
    @Composable
    @ReadOnlyComposable
    get() = if (LocalInnovateDarkTheme.current) InnovateBorderDark else InnovateBorderLight
