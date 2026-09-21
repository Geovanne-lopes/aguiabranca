package br.com.fiap.challengeaguiabranca.domain.repository

import br.com.fiap.challengeaguiabranca.domain.model.User
import br.com.fiap.challengeaguiabranca.domain.model.UserRole

interface AuthRepository {
    suspend fun login(email: String, password: String): User
    suspend fun register(name: String, email: String, password: String, role: UserRole)
    suspend fun resetPassword(email: String, newPassword: String)
    suspend fun logout()
    suspend fun restoreSession(): User?
}
