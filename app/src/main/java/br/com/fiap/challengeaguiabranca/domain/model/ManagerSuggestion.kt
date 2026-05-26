package br.com.fiap.challengeaguiabranca.domain.model

data class ManagerSuggestion(
    val id: String,
    val managerName: String,
    val targetAuthorId: String,
    val targetEmail: String,
    val targetName: String,
    val message: String,
    val createdAtEpochMillis: Long
)
