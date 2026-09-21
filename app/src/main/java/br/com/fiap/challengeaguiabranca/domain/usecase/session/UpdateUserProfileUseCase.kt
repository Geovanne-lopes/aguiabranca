package br.com.fiap.challengeaguiabranca.domain.usecase.session

import br.com.fiap.challengeaguiabranca.domain.repository.SessionRepository
import br.com.fiap.challengeaguiabranca.domain.repository.UserRepository

class UpdateUserProfileUseCase(
    private val sessionRepository: SessionRepository,
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(
        name: String,
        email: String,
        avatarUrl: String?
    ): Result<Unit> {
        val current = sessionRepository.getCurrentUser()
            ?: return Result.failure(IllegalStateException("Sessão inválida."))

        val trimmedName = name.trim()
        val trimmedEmail = email.trim()

        when {
            trimmedName.length < 2 ->
                return Result.failure(IllegalArgumentException("Nome deve ter pelo menos 2 caracteres."))
            !trimmedEmail.contains("@") ->
                return Result.failure(IllegalArgumentException("Informe um e-mail válido."))
        }

        return runCatching {
            val updated = userRepository.updateProfile(
                name = trimmedName,
                email = trimmedEmail,
                avatarUrl = avatarUrl
            )
            sessionRepository.saveSession(updated)
        }
    }
}
