package br.com.fiap.challengeaguiabranca.domain.usecase.auth

import br.com.fiap.challengeaguiabranca.domain.repository.AuthRepository

class ResetPasswordUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String, newPassword: String) {
        authRepository.resetPassword(email.trim(), newPassword)
    }
}
