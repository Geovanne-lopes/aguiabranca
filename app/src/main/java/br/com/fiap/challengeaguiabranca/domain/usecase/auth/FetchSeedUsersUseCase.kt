package br.com.fiap.challengeaguiabranca.domain.usecase.auth

import br.com.fiap.challengeaguiabranca.domain.model.User
import br.com.fiap.challengeaguiabranca.domain.repository.UserRepository

class FetchSeedUsersUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(): List<User> = userRepository.fetchSeedUsers()
}
