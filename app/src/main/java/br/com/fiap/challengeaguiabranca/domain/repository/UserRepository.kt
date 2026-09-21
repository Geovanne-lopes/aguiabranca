package br.com.fiap.challengeaguiabranca.domain.repository

import br.com.fiap.challengeaguiabranca.domain.model.User
import br.com.fiap.challengeaguiabranca.domain.model.UserRole

interface UserRepository {
    suspend fun fetchSeedUsers(): List<User>
    suspend fun listByRole(role: UserRole): List<User>
    suspend fun updateProfile(name: String, email: String, avatarUrl: String?): User
}
