package br.com.fiap.challengeaguiabranca.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import br.com.fiap.challengeaguiabranca.ui.theme.InnovatePrimary
import br.com.fiap.challengeaguiabranca.ui.theme.InnovateTextSecondary

private val ChartHeight = 140.dp
private val BarWidth = 32.dp
private val BarMaxFraction = 0.85f

@Composable
fun SimpleBarChart(
    values: List<Int>,
    labels: List<String> = emptyList(),
    highlightLast: Boolean = true,
    modifier: Modifier = Modifier,
    barColor: Color = InnovatePrimary,
    inactiveBarColor: Color = InnovateTextSecondary.copy(alpha = 0.25f)
) {
    if (values.isEmpty()) return
    val max = values.maxOrNull()?.coerceAtLeast(1) ?: 1
    val showLabels = labels.size == values.size && labels.isNotEmpty()

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(ChartHeight),
            verticalAlignment = Alignment.Bottom
        ) {
            values.forEachIndexed { index, value ->
                val color = if (highlightLast && index == values.lastIndex) barColor else inactiveBarColor
                val barHeight = ChartHeight * BarMaxFraction * (value.toFloat() / max)

                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom
                ) {
                    if (value > 0) {
                        Box(
                            modifier = Modifier
                                .width(BarWidth)
                                .height(barHeight)
                                .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                                .background(color)
                        )
                    }
                }
            }
        }

        if (showLabels) {
            Row(modifier = Modifier.fillMaxWidth()) {
                labels.forEach { label ->
                    Text(
                        text = label,
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.labelSmall,
                        color = InnovateTextSecondary,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}
