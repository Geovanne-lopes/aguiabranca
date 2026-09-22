package br.com.fiap.challengeaguiabranca.domain.model

data class Idea(
    val id: String,
    val title: String,
    val description: String,
    val category: IdeaCategory,
    val authorId: String,
    val status: IdeaStatus = IdeaStatus.PENDING,
    val createdAtEpochMillis: Long = System.currentTimeMillis(),
    val guidelineId: String? = null,
    val guidelineTitle: String? = null,
    val authorName: String? = null
)
