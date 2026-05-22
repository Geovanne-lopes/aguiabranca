package br.com.fiap.challengeaguiabranca.domain.usecase.auth

import br.com.fiap.challengeaguiabranca.domain.auth.AuthCatalog
import br.com.fiap.challengeaguiabranca.domain.auth.AuthException
import br.com.fiap.challengeaguiabranca.domain.model.User
import br.com.fiap.challengeaguiabranca.domain.usecase.session.SaveUserSessionUseCase

/**
 * Autentica e-mail + senha contra o catálogo fixo e associa ao usuário da FakerAPI do mesmo perfil.
 * Não permite escolher perfil após o login — o papel é definido pela conta.
 */
class AuthenticateUserUseCase(
    private val fetchSeedUsersUseCase: FetchSeedUsersUseCase,
    private val saveUserSessionUseCase: SaveUserSessionUseCase
) {
    suspend operator fun invoke(email: String, password: String): User {
        val account = AuthCatalog.findByEmail(email)
            ?: throw AuthException.UnauthorizedEmail()

        if (password != account.password) {
            throw AuthException.InvalidPassword()
        }

        val seedUsers = runCatching { fetchSeedUsersUseCase() }
            .getOrElse { throw AuthException.UserDataUnavailable() }

        val apiUser = seedUsers.find { it.role == account.role }
            ?: throw AuthException.UserDataUnavailable()

        val sessionUser = apiUser.copy(email = account.email)
        saveUserSessionUseCase(sessionUser)
        return sessionUser
    }
}
