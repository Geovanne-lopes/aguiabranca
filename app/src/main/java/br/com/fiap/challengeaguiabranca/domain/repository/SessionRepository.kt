package br.com.fiap.challengeaguiabranca.domain.repository

import br.com.fiap.challengeaguiabranca.domain.model.User
import kotlinx.coroutines.flow.Flow

interface SessionRepository {
    fun observeCurrentUser(): Flow<User?>
    suspend fun getCurrentUser(): User?
    suspend fun saveSession(user: User)
    suspend fun clearSession()
    suspend fun isLoggedIn(): Boolean
}
