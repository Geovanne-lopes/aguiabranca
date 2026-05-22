package br.com.fiap.challengeaguiabranca.ui.feature.operator.home

import br.com.fiap.challengeaguiabranca.domain.model.Idea
import br.com.fiap.challengeaguiabranca.domain.model.IdeaStatus
import java.time.ZoneId
import java.time.ZonedDateTime
import kotlin.math.roundToInt

/**
 * Calcula tendência real comparando ideias do mês atual vs. mês anterior (por [Idea.createdAtEpochMillis]).
 */
object OperatorKpiTrend {

    fun submittedTrend(ideas: List<Idea>): String =
        monthTrend(ideas) { true }

    fun approvedTrend(ideas: List<Idea>): String =
        monthTrend(ideas) { it.status == IdeaStatus.APPROVED }

    private fun monthTrend(
        ideas: List<Idea>,
        predicate: (Idea) -> Boolean
    ): String {
        val zone = ZoneId.systemDefault()
        val now = ZonedDateTime.now(zone)

        val currentMonthStart = now.withDayOfMonth(1).toLocalDate()
            .atStartOfDay(zone).toInstant().toEpochMilli()
        val nextMonthStart = now.plusMonths(1).withDayOfMonth(1).toLocalDate()
            .atStartOfDay(zone).toInstant().toEpochMilli()
        val previousMonthStart = now.minusMonths(1).withDayOfMonth(1).toLocalDate()
            .atStartOfDay(zone).toInstant().toEpochMilli()

        val filtered = ideas.filter(predicate)
        val currentMonthCount = filtered.count { idea ->
            idea.createdAtEpochMillis in currentMonthStart until nextMonthStart
        }
        val previousMonthCount = filtered.count { idea ->
            idea.createdAtEpochMillis in previousMonthStart until currentMonthStart
        }

        return formatTrend(currentMonthCount, previousMonthCount)
    }

    private fun formatTrend(current: Int, previous: Int): String = when {
        current == 0 && previous == 0 -> "Sem dados no período"
        previous == 0 && current > 0 -> "Primeiro registro neste mês"
        current == 0 && previous > 0 -> "↓ 100% vs mês anterior"
        else -> {
            val percent = ((current - previous).toDouble() / previous * 100.0).roundToInt()
            when {
                percent > 0 -> "↑ $percent% vs mês anterior"
                percent < 0 -> "↓ ${-percent}% vs mês anterior"
                else -> "Igual ao mês anterior"
            }
        }
    }
}
