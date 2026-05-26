package br.com.fiap.challengeaguiabranca.ui.util

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

private const val MotionDuration = 520

fun <S> premiumTabTransform(): AnimatedContentTransitionScope<S>.() -> ContentTransform = {
    (slideIntoContainer(
        towards = AnimatedContentTransitionScope.SlideDirection.Left,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    ) + fadeIn(animationSpec = tween(MotionDuration / 2)))
        .togetherWith(
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(MotionDuration)
            ) + fadeOut(animationSpec = tween(MotionDuration / 2))
        )
}

@Composable
fun Modifier.premiumListEntrance(index: Int = 0): Modifier {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(index.coerceAtMost(10) * 55L)
        visible = true
    }
    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(MotionDuration / 2),
        label = "premiumAlpha"
    )
    val offset by animateDpAsState(
        targetValue = if (visible) 0.dp else 38.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "premiumOffset"
    )
    val scale by animateFloatAsState(
        targetValue = if (visible) 1f else 0.86f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "premiumScale"
    )
    val rotation by animateFloatAsState(
        targetValue = if (visible) 0f else -2.5f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "premiumRotation"
    )
    return this
        .alpha(alpha)
        .graphicsLayer {
            translationY = offset.toPx()
            scaleX = scale
            scaleY = scale
            rotationZ = rotation
        }
}

@Composable
fun Modifier.pressAura(interactionSource: MutableInteractionSource): Modifier {
    val pressed by interactionSource.collectIsPressedAsState()
    val auraAlpha by animateFloatAsState(
        targetValue = if (pressed) 0.36f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "pressAuraBorder"
    )
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.96f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "pressAuraScale"
    )
    val elevation by animateDpAsState(
        targetValue = if (pressed) 18.dp else 0.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "pressAuraElevation"
    )
    return this
        .shadow(elevation = elevation, shape = RoundedCornerShape(18.dp))
        .border(
            width = 1.dp,
            color = Color(0xFFFF4FA3).copy(alpha = auraAlpha),
            shape = RoundedCornerShape(18.dp)
        )
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
}

@Composable
fun NavAuraIcon(
    selected: Boolean,
    color: Color,
    content: @Composable () -> Unit
) {
    val auraAlpha by animateFloatAsState(
        targetValue = if (selected) 0.56f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "navAuraAlpha"
    )
    val auraSize by animateDpAsState(
        targetValue = if (selected) 42.dp else 24.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "navAuraSize"
    )
    Box(
        modifier = Modifier.size(44.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(auraSize)
                .border(1.dp, color.copy(alpha = auraAlpha), CircleShape)
        )
        content()
    }
}
