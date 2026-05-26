package br.com.fiap.challengeaguiabranca.domain.model

data class OperatorActivity(
    val authorId: String,
    val name: String,
    val email: String,
    val ideasSubmitted: Int,
    val ideasApproved: Int
)
