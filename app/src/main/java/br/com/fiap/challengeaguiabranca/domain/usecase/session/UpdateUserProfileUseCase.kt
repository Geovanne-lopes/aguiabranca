package br.com.fiap.challengeaguiabranca.domain.usecase.session

import br.com.fiap.challengeaguiabranca.domain.repository.SessionRepository

class UpdateUserProfileUseCase(
    private val sessionRepository: SessionRepository
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

        sessionRepository.saveSession(
            current.copy(
                name = trimmedName,
                email = trimmedEmail,
                avatarUrl = avatarUrl
            )
        )
        return Result.success(Unit)
    }
}
