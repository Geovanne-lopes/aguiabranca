package br.com.fiap.challengeaguiabranca.domain.model

enum class OperatorNotificationType {
    GUIDELINE,
    IDEA_PENDING,
    IDEA_APPROVED,
    IDEA_REJECTED,
    IDEA_PRIORITIZED
}

data class OperatorNotification(
    val id: String,
    val title: String,
    val body: String,
    val type: OperatorNotificationType,
    val timestampEpochMillis: Long
)
