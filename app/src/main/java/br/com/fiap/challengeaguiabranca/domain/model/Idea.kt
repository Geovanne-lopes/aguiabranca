package br.com.fiap.challengeaguiabranca.domain.model

/**
 * Ideia ou problema registrado por um operador.
 */
data class Idea(
    val id: String,
    val title: String,
    val description: String,
    val category: IdeaCategory,
    val authorId: String,
    val status: IdeaStatus = IdeaStatus.PENDING,
    val createdAtEpochMillis: Long = System.currentTimeMillis()
)
