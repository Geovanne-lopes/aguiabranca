package br.com.fiap.challengeaguiabranca.domain.usecase.auth

import br.com.fiap.challengeaguiabranca.domain.model.User
import br.com.fiap.challengeaguiabranca.domain.repository.AuthRepository
import br.com.fiap.challengeaguiabranca.domain.repository.SessionRepository

class RestoreSessionUseCase(
    private val authRepository: AuthRepository,
    private val sessionRepository: SessionRepository
) {
    suspend operator fun invoke(): User? {
        val user = authRepository.restoreSession() ?: return null
        sessionRepository.saveSession(user)
        return user
    }
}
