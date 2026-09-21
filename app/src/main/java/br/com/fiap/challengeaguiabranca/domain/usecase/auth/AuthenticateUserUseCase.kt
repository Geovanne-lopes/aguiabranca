package br.com.fiap.challengeaguiabranca.domain.usecase.auth

import br.com.fiap.challengeaguiabranca.domain.model.User
import br.com.fiap.challengeaguiabranca.domain.repository.AuthRepository
import br.com.fiap.challengeaguiabranca.domain.usecase.session.SaveUserSessionUseCase

/**
 * Autentica na API, grava o par de tokens e a sessão local do perfil.
 */
class AuthenticateUserUseCase(
    private val authRepository: AuthRepository,
    private val saveUserSessionUseCase: SaveUserSessionUseCase
) {
    suspend operator fun invoke(email: String, password: String): User {
        val user = authRepository.login(email.trim(), password)
        saveUserSessionUseCase(user)
        return user
    }
}
