package br.com.fiap.challengeaguiabranca.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import br.com.fiap.challengeaguiabranca.domain.model.UserRole

data class RoleAccent(
    val accent: Color,
    val accentDark: Color,
    val accentSoft: Color,
    val gradientStart: Color,
    val gradientEnd: Color
) {
    fun horizontalGradient(): Brush = Brush.horizontalGradient(listOf(gradientStart, gradientEnd))

    fun verticalGradient(): Brush = Brush.verticalGradient(listOf(gradientStart, gradientEnd))

    companion object {
        val Default = RoleAccent(
            accent = InnovatePrimary,
            accentDark = InnovatePrimaryDark,
            accentSoft = InnovatePrimary.copy(alpha = 0.12f),
            gradientStart = InnovatePrimary,
            gradientEnd = InnovatePrimaryDark
        )

        fun forRole(role: UserRole?): RoleAccent = when (role) {
            UserRole.OPERATOR -> RoleAccent(
                accent = InnovateOperatorBlue,
                accentDark = InnovateOperatorBlueDark,
                accentSoft = InnovateOperatorBlue.copy(alpha = 0.14f),
                gradientStart = Color(0xFF2563EB),
                gradientEnd = InnovatePrimary
            )
            UserRole.MANAGER -> RoleAccent(
                accent = InnovateManagerPink,
                accentDark = InnovateManagerPinkDark,
                accentSoft = InnovateManagerPink.copy(alpha = 0.14f),
                gradientStart = InnovateManagerPink,
                gradientEnd = Color(0xFFF97316)
            )
            UserRole.LEADER -> RoleAccent(
                accent = InnovateLeaderPurple,
                accentDark = InnovateLeaderPurpleDark,
                accentSoft = InnovateLeaderPurple.copy(alpha = 0.14f),
                gradientStart = InnovateLeaderPurple,
                gradientEnd = Color(0xFF2563EB)
            )
            null -> Default
        }
    }
}

val LocalRoleAccent = staticCompositionLocalOf { RoleAccent.Default }

@Composable
fun ProvideRoleAccent(
    role: UserRole?,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(LocalRoleAccent provides RoleAccent.forRole(role)) {
        content()
    }
}

@Composable
fun currentRoleAccent(): RoleAccent = LocalRoleAccent.current
