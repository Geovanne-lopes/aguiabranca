package br.com.fiap.challengeaguiabranca.domain.model

data class User(
    val id: String,
    val name: String,
    val email: String,
    val avatarUrl: String?,
    val role: UserRole
)
