package br.com.fiap.challengeaguiabranca.domain.usecase.auth

import br.com.fiap.challengeaguiabranca.domain.model.UserRole
import br.com.fiap.challengeaguiabranca.domain.repository.AuthRepository

class RegisterAccountUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(
        name: String,
        email: String,
        password: String,
        role: UserRole
    ) {
        authRepository.register(
            name = name.trim(),
            email = email.trim(),
            password = password,
            role = role
        )
    }
}
