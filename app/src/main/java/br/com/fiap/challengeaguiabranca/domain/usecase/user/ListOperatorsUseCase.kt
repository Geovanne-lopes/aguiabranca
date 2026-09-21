package br.com.fiap.challengeaguiabranca.domain.usecase.user

import br.com.fiap.challengeaguiabranca.domain.model.User
import br.com.fiap.challengeaguiabranca.domain.model.UserRole
import br.com.fiap.challengeaguiabranca.domain.repository.UserRepository

class ListOperatorsUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(): List<User> = userRepository.listByRole(UserRole.OPERATOR)
}
