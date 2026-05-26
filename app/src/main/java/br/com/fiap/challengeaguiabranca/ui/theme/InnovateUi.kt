package br.com.fiap.challengeaguiabranca.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

object InnovateShapes {
    val Small = RoundedCornerShape(10.dp)
    val Medium = RoundedCornerShape(14.dp)
    val Large = RoundedCornerShape(20.dp)
    val ExtraLarge = RoundedCornerShape(26.dp)
    val Pill = RoundedCornerShape(999.dp)
}

@Composable
fun innovateBackgroundColor(): Color =
    if (MaterialTheme.colorScheme.background == InnovateBackgroundDark) {
        InnovateBackgroundDark
    } else {
        InnovateBackground
    }

@Composable
fun innovateSurfaceColor(): Color = MaterialTheme.colorScheme.surface

@Composable
fun innovateSurface2Color(): Color =
    if (MaterialTheme.colorScheme.background == InnovateBackgroundDark) {
        InnovateSurface2Dark
    } else {
        InnovateSurface2
    }

@Composable
fun innovateBorderColor(): Color =
    if (MaterialTheme.colorScheme.background == InnovateBackgroundDark) {
        InnovateBorderDark
    } else {
        InnovateBorder
    }

@Composable
fun innovateLinkColor(): Color =
    if (MaterialTheme.colorScheme.background == InnovateBackgroundDark) {
        InnovateLinkDark
    } else {
        InnovateLink
    }

@Composable
fun InnovateScreenBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val isDark = MaterialTheme.colorScheme.background == InnovateBackgroundDark
    val base = if (isDark) InnovateBackgroundDark else InnovateBackground
    val brush = if (isDark) {
        Brush.radialGradient(
            colors = listOf(InnovatePrimary.copy(alpha = 0.2f), Color.Transparent),
            radius = 900f
        )
    } else {
        Brush.radialGradient(
            colors = listOf(InnovatePrimary.copy(alpha = 0.16f), Color.Transparent),
            radius = 800f
        )
    }
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(base)
            .background(brush)
    ) {
        content()
    }
}

@Composable
fun InnovateCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier
            .shadow(
                elevation = 8.dp,
                shape = InnovateShapes.Large,
                ambientColor = Color(0xFF201F1C).copy(alpha = 0.06f),
                spotColor = Color(0xFF201F1C).copy(alpha = 0.06f)
            )
            .border(1.dp, innovateBorderColor(), InnovateShapes.Large),
        shape = InnovateShapes.Large,
        colors = CardDefaults.cardColors(containerColor = innovateSurfaceColor()),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        content = content
    )
}
