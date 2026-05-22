package br.com.fiap.challengeaguiabranca.domain.model

/**
 * Usuário autenticado no app.
 * Dados iniciais podem vir do FakerAPI; [role] define o fluxo de navegação.
 */
data class User(
    val id: String,
    val name: String,
    val email: String,
    val avatarUrl: String?,
    val role: UserRole
)
