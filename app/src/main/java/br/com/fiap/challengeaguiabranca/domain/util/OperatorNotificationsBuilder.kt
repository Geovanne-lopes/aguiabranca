package br.com.fiap.challengeaguiabranca.domain.util

import br.com.fiap.challengeaguiabranca.domain.model.Idea
import br.com.fiap.challengeaguiabranca.domain.model.IdeaStatus
import br.com.fiap.challengeaguiabranca.domain.model.OperatorNotification
import br.com.fiap.challengeaguiabranca.domain.model.OperatorNotificationType
import br.com.fiap.challengeaguiabranca.domain.model.ManagerSuggestion
import br.com.fiap.challengeaguiabranca.domain.model.StrategicGuideline

object OperatorNotificationsBuilder {

    fun build(
        guidelines: List<StrategicGuideline>,
        ideas: List<Idea>,
        suggestions: List<ManagerSuggestion> = emptyList()
    ): List<OperatorNotification> {
        val notifications = mutableListOf<OperatorNotification>()

        guidelines.forEach { guideline ->
            notifications += OperatorNotification(
                id = "guideline-${guideline.id}",
                title = "Orientação da liderança",
                body = "A liderança publicou: \"${guideline.title}\". ${guideline.content}",
                type = OperatorNotificationType.GUIDELINE,
                timestampEpochMillis = guideline.updatedAtEpochMillis
            )
        }

        suggestions.forEach { suggestion ->
            notifications += OperatorNotification(
                id = "suggestion-${suggestion.id}",
                title = "Sugestão do gestor",
                body = "${suggestion.managerName} sugeriu: ${suggestion.message}",
                type = OperatorNotificationType.GUIDELINE,
                timestampEpochMillis = suggestion.createdAtEpochMillis
            )
        }

        ideas.forEach { idea ->
            val (title, body, type) = when (idea.status) {
                IdeaStatus.PENDING -> Triple(
                    "Ideia enviada",
                    "Sua ideia \"${idea.title}\" está aguardando curadoria do gestor.",
                    OperatorNotificationType.IDEA_PENDING
                )
                IdeaStatus.APPROVED -> Triple(
                    "Ideia aprovada",
                    "Parabéns! \"${idea.title}\" foi aprovada. Confira na aba Ideias.",
                    OperatorNotificationType.IDEA_APPROVED
                )
                IdeaStatus.REJECTED -> Triple(
                    "Ideia reprovada",
                    "A ideia \"${idea.title}\" foi reprovada. Você pode enviar uma nova versão.",
                    OperatorNotificationType.IDEA_REJECTED
                )
                IdeaStatus.PRIORITIZED -> Triple(
                    "Ideia priorizada",
                    "Sua ideia \"${idea.title}\" foi priorizada pela gestão.",
                    OperatorNotificationType.IDEA_PRIORITIZED
                )
            }
            notifications += OperatorNotification(
                id = "idea-${idea.status.name.lowercase()}-${idea.id}",
                title = title,
                body = body,
                type = type,
                timestampEpochMillis = idea.createdAtEpochMillis
            )
        }

        return notifications.sortedByDescending { it.timestampEpochMillis }
    }
}
