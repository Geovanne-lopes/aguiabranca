package br.com.fiap.challengeaguiabranca.domain.model

enum class ManagerNotificationType {
    PENDING_IDEAS,
    NEW_IDEA,
    SUGGESTION_SENT,
    TEAM_ACTIVITY
}

data class ManagerNotification(
    val id: String,
    val title: String,
    val body: String,
    val type: ManagerNotificationType,
    val timestampEpochMillis: Long
)
