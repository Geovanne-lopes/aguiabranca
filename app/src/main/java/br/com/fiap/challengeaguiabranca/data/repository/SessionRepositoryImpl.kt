package br.com.fiap.challengeaguiabranca.data.repository

import br.com.fiap.challengeaguiabranca.data.local.dao.SessionDao
import br.com.fiap.challengeaguiabranca.data.local.mapper.SessionEntityMapper
import br.com.fiap.challengeaguiabranca.domain.model.User
import br.com.fiap.challengeaguiabranca.domain.repository.SessionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SessionRepositoryImpl(
    private val sessionDao: SessionDao
) : SessionRepository {

    override fun observeCurrentUser(): Flow<User?> =
        sessionDao.observeSession().map { entity ->
            entity?.let(SessionEntityMapper::toDomain)
        }

    override suspend fun getCurrentUser(): User? =
        sessionDao.getSession()?.let(SessionEntityMapper::toDomain)

    override suspend fun saveSession(user: User) {
        sessionDao.saveSession(SessionEntityMapper.toEntity(user))
    }

    override suspend fun clearSession() {
        sessionDao.clearSession()
    }

    override suspend fun isLoggedIn(): Boolean =
        sessionDao.getSession() != null
}
