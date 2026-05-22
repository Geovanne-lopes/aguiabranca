package br.com.fiap.challengeaguiabranca.ui.feature.manager.home

import br.com.fiap.challengeaguiabranca.domain.model.Idea
import br.com.fiap.challengeaguiabranca.domain.model.IdeaStatus
import br.com.fiap.challengeaguiabranca.domain.model.Project
import br.com.fiap.challengeaguiabranca.domain.model.ProjectStatus
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.TextStyle
import java.util.Locale
import kotlin.math.roundToInt

object ManagerDashboardStats {

    fun buildMonthlyBars(ideas: List<Idea>, months: Int = 5): List<MonthBarData> {
        val zone = ZoneId.systemDefault()
        val now = ZonedDateTime.now(zone)
        return (months - 1 downTo 0).map { offset ->
            val month = now.minusMonths(offset.toLong())
            val start = month.withDayOfMonth(1).toLocalDate().atStartOfDay(zone).toInstant().toEpochMilli()
            val end = month.plusMonths(1).withDayOfMonth(1).toLocalDate().atStartOfDay(zone).toInstant().toEpochMilli()
            val label = month.month.getDisplayName(TextStyle.SHORT, Locale("pt", "BR"))
                .replaceFirstChar { it.uppercase() }
            val count = ideas.count { it.createdAtEpochMillis in start until end }
            MonthBarData(label = label, count = count)
        }
    }

    fun approvalRatePercent(ideas: List<Idea>): Int {
        if (ideas.isEmpty()) return 0
        val approved = ideas.count {
            it.status == IdeaStatus.APPROVED || it.status == IdeaStatus.PRIORITIZED
        }
        return ((approved.toDouble() / ideas.size) * 100).roundToInt()
    }

    fun activeProjectsCount(projects: List<Project>): Int =
        projects.count { it.status != ProjectStatus.COMPLETED }

    fun monthComparisonTrend(current: Int, previous: Int): String = when {
        current == 0 && previous == 0 -> "Sem dados no período"
        previous == 0 && current > 0 -> "Primeiro registro neste mês"
        else -> {
            val percent = ((current - previous).toDouble() / previous * 100).roundToInt()
            when {
                percent > 0 -> "↑ $percent% vs mês anterior"
                percent < 0 -> "↓ ${-percent}% vs mês anterior"
                else -> "Igual ao mês anterior"
            }
        }
    }

    fun ideasThisMonth(ideas: List<Idea>): Int {
        val zone = ZoneId.systemDefault()
        val now = ZonedDateTime.now(zone)
        val start = now.withDayOfMonth(1).toLocalDate().atStartOfDay(zone).toInstant().toEpochMilli()
        val end = now.plusMonths(1).withDayOfMonth(1).toLocalDate().atStartOfDay(zone).toInstant().toEpochMilli()
        return ideas.count { it.createdAtEpochMillis in start until end }
    }

    fun ideasPreviousMonth(ideas: List<Idea>): Int {
        val zone = ZoneId.systemDefault()
        val now = ZonedDateTime.now(zone)
        val start = now.minusMonths(1).withDayOfMonth(1).toLocalDate().atStartOfDay(zone).toInstant().toEpochMilli()
        val end = now.withDayOfMonth(1).toLocalDate().atStartOfDay(zone).toInstant().toEpochMilli()
        return ideas.count { it.createdAtEpochMillis in start until end }
    }
}
