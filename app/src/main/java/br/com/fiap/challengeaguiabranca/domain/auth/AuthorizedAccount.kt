package br.com.fiap.challengeaguiabranca.domain.auth

import br.com.fiap.challengeaguiabranca.domain.model.UserRole

/**
 * Contas demo só para preencher e-mail e senha nos botões da tela de login.
 * A senha não é validada aqui: o login, o cadastro e o reset vão para a API.
 */
data class AuthorizedAccount(
    val email: String,
    val password: String,
    val role: UserRole
)

object AuthCatalog {

    private val baseAccounts = listOf(
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

    private val registeredAccounts = mutableListOf<AuthorizedAccount>()

    private val accounts: List<AuthorizedAccount>
        get() = baseAccounts + registeredAccounts

    fun findByEmail(email: String): AuthorizedAccount? =
        accounts.find { it.email.equals(email.trim(), ignoreCase = true) }

    fun register(email: String, password: String, role: UserRole): Boolean {
        if (findByEmail(email) != null) return false
        registeredAccounts.add(
            AuthorizedAccount(
                email = email.trim().lowercase(),
                password = password,
                role = role
            )
        )
        return true
    }

    fun resetPassword(email: String, password: String): Boolean {
        val cleanEmail = email.trim().lowercase()
        val registeredIndex = registeredAccounts.indexOfFirst { it.email.equals(cleanEmail, ignoreCase = true) }
        if (registeredIndex >= 0) {
            registeredAccounts[registeredIndex] = registeredAccounts[registeredIndex].copy(password = password)
            return true
        }
        val base = baseAccounts.find { it.email.equals(cleanEmail, ignoreCase = true) } ?: return false
        registeredAccounts.add(base.copy(password = password))
        return true
    }
}
