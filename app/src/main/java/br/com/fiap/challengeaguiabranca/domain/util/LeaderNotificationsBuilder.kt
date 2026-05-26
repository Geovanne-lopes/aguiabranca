package br.com.fiap.challengeaguiabranca.domain.util

import br.com.fiap.challengeaguiabranca.domain.catalog.MockOperatorsCatalog
import br.com.fiap.challengeaguiabranca.domain.model.Idea
import br.com.fiap.challengeaguiabranca.domain.model.IdeaStatus
import br.com.fiap.challengeaguiabranca.domain.model.ManagerNotification
import br.com.fiap.challengeaguiabranca.domain.model.ManagerNotificationType
import br.com.fiap.challengeaguiabranca.domain.model.OperatorActivity
import br.com.fiap.challengeaguiabranca.domain.model.StrategicGuideline
import java.util.Calendar

object LeaderNotificationsBuilder {

    fun build(
        ideas: List<Idea>,
        ranking: List<OperatorActivity>,
        guidelines: List<StrategicGuideline>
    ): List<ManagerNotification> {
        val notifications = mutableListOf<ManagerNotification>()
        val now = System.currentTimeMillis()
        val pending = ideas.count { it.status == IdeaStatus.PENDING }
        val approved = ideas.count {
            it.status == IdeaStatus.APPROVED || it.status == IdeaStatus.PRIORITIZED
        }

        notifications += ManagerNotification(
            id = "overview-${ideas.size}-$pending-$approved",
            title = "Visão estratégica",
            body = "${ideas.size} ideias no portfólio · $pending pendentes · $approved aprovadas.",
            type = ManagerNotificationType.TEAM_ACTIVITY,
            timestampEpochMillis = now
        )

        val startOfMonth = startOfCurrentMonth()
        val monthlyRanking = ideas
            .filter { it.createdAtEpochMillis >= startOfMonth }
            .groupBy { it.authorId }
            .map { (authorId, list) -> authorId to list.size }
            .sortedByDescending { it.second }

        monthlyRanking.firstOrNull()?.let { (authorId, count) ->
            val name = MockOperatorsCatalog.resolveName(authorId)
            notifications += ManagerNotification(
                id = "monthly-$authorId-$count",
                title = "Destaque do mês",
                body = "$name enviou $count ideia(s) neste mês.",
                type = ManagerNotificationType.TEAM_ACTIVITY,
                timestampEpochMillis = now - 60_000
            )
        }

        ideas
            .filter { it.status == IdeaStatus.PENDING }
            .take(3)
            .forEach { idea ->
                notifications += ManagerNotification(
                    id = "idea-${idea.id}",
                    title = "Ideia pendente de curadoria",
                    body = "\"${idea.title}\" — ${MockOperatorsCatalog.resolveName(idea.authorId)}",
                    type = ManagerNotificationType.NEW_IDEA,
                    timestampEpochMillis = idea.createdAtEpochMillis
                )
            }

        guidelines.take(2).forEach { g ->
            notifications += ManagerNotification(
                id = "guideline-${g.id}",
                title = "Diretriz publicada",
                body = g.title,
                type = ManagerNotificationType.SUGGESTION_SENT,
                timestampEpochMillis = g.updatedAtEpochMillis
            )
        }

        return notifications.sortedByDescending { it.timestampEpochMillis }
    }

    private fun startOfCurrentMonth(): Long {
        val cal = Calendar.getInstance()
        cal.set(Calendar.DAY_OF_MONTH, 1)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }
}
