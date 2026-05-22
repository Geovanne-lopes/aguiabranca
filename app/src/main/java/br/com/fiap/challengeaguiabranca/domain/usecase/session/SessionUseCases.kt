package br.com.fiap.challengeaguiabranca.domain.usecase.session

import br.com.fiap.challengeaguiabranca.domain.model.User
import br.com.fiap.challengeaguiabranca.domain.repository.SessionRepository
import kotlinx.coroutines.flow.Flow

class SaveUserSessionUseCase(
    private val sessionRepository: SessionRepository
) {
    suspend operator fun invoke(user: User) {
        sessionRepository.saveSession(user)
    }
}

class GetCurrentUserUseCase(
    private val sessionRepository: SessionRepository
) {
    suspend operator fun invoke(): User? = sessionRepository.getCurrentUser()
}

class ObserveCurrentUserUseCase(
    private val sessionRepository: SessionRepository
) {
    operator fun invoke(): Flow<User?> = sessionRepository.observeCurrentUser()
}

class ClearSessionUseCase(
    private val sessionRepository: SessionRepository
) {
    suspend operator fun invoke() {
        sessionRepository.clearSession()
    }
}

class IsLoggedInUseCase(
    private val sessionRepository: SessionRepository
) {
    suspend operator fun invoke(): Boolean = sessionRepository.isLoggedIn()
}
