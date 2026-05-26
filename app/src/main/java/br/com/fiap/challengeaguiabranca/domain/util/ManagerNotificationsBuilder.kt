package br.com.fiap.challengeaguiabranca.domain.util

import br.com.fiap.challengeaguiabranca.domain.model.Idea
import br.com.fiap.challengeaguiabranca.domain.model.IdeaStatus
import br.com.fiap.challengeaguiabranca.domain.model.ManagerNotification
import br.com.fiap.challengeaguiabranca.domain.model.ManagerNotificationType
import br.com.fiap.challengeaguiabranca.domain.model.ManagerSuggestion
import br.com.fiap.challengeaguiabranca.domain.catalog.MockOperatorsCatalog

object ManagerNotificationsBuilder {

    fun build(
        ideas: List<Idea>,
        recentSuggestions: List<ManagerSuggestion>
    ): List<ManagerNotification> {
        val notifications = mutableListOf<ManagerNotification>()
        val now = System.currentTimeMillis()
        val pending = ideas.count { it.status == IdeaStatus.PENDING }

        if (pending > 0) {
            notifications += ManagerNotification(
                id = "pending-$pending",
                title = "Ideias aguardando curadoria",
                body = "Você tem $pending ideia(s) pendente(s) para avaliar na aba Curadoria.",
                type = ManagerNotificationType.PENDING_IDEAS,
                timestampEpochMillis = now
            )
        }

        ideas
            .filter { it.status == IdeaStatus.PENDING }
            .take(5)
            .forEach { idea ->
                val author = MockOperatorsCatalog.resolveName(idea.authorId)
                notifications += ManagerNotification(
                    id = "new-${idea.id}",
                    title = "Nova ideia de $author",
                    body = "\"${idea.title}\" — aguarda sua avaliação.",
                    type = ManagerNotificationType.NEW_IDEA,
                    timestampEpochMillis = idea.createdAtEpochMillis
                )
            }

        recentSuggestions.take(3).forEach { suggestion ->
            notifications += ManagerNotification(
                id = "suggestion-${suggestion.id}",
                title = "Sugestão enviada",
                body = "Para ${suggestion.targetName}: ${suggestion.message}",
                type = ManagerNotificationType.SUGGESTION_SENT,
                timestampEpochMillis = suggestion.createdAtEpochMillis
            )
        }

        val topAuthor = ideas.groupBy { it.authorId }
            .maxByOrNull { it.value.size }
        topAuthor?.let { (authorId, authorIdeas) ->
            val name = MockOperatorsCatalog.resolveName(authorId)
            notifications += ManagerNotification(
                id = "top-$authorId",
                title = "Operador mais ativo",
                body = "$name lidera com ${authorIdeas.size} ideia(s) enviada(s).",
                type = ManagerNotificationType.TEAM_ACTIVITY,
                timestampEpochMillis = now - 3_600_000
            )
        }

        return notifications.sortedByDescending { it.timestampEpochMillis }
    }
}
