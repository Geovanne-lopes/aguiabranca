package br.com.fiap.challengeaguiabranca.domain.auth

import br.com.fiap.challengeaguiabranca.domain.model.UserRole

/**
 * Contas fixas do desafio: cada e-mail pertence a um único [UserRole].
 * A senha é validada localmente; nome/avatar vêm da FakerAPI após autenticação.
 */
data class AuthorizedAccount(
    val email: String,
    val password: String,
    val role: UserRole
)

object AuthCatalog {

    private val accounts = listOf(
        AuthorizedAccount(
            email = "operador@innovatecorp.com",
            password = "oper123",
            role = UserRole.OPERATOR
        ),
        AuthorizedAccount(
            email = "gestor@innovatecorp.com",
            password = "gest123",
            role = UserRole.MANAGER
        ),
        AuthorizedAccount(
            email = "lideranca@innovatecorp.com",
            password = "lider123",
            role = UserRole.LEADER
        )
    )

    fun findByEmail(email: String): AuthorizedAccount? =
        accounts.find { it.email.equals(email.trim(), ignoreCase = true) }
}
