package br.com.fiap.challengeaguiabranca.data.repository

import br.com.fiap.challengeaguiabranca.data.local.dao.IdeaDao
import br.com.fiap.challengeaguiabranca.data.local.mapper.IdeaEntityMapper
import br.com.fiap.challengeaguiabranca.domain.model.Idea
import br.com.fiap.challengeaguiabranca.domain.model.IdeaStatus
import br.com.fiap.challengeaguiabranca.domain.repository.IdeaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class IdeaRepositoryImpl(
    private val ideaDao: IdeaDao
) : IdeaRepository {

    override fun observeAll(): Flow<List<Idea>> =
        ideaDao.observeAll().map(IdeaEntityMapper::toDomainList)

    override fun observeByAuthor(authorId: String): Flow<List<Idea>> =
        ideaDao.observeByAuthor(authorId).map(IdeaEntityMapper::toDomainList)

    override fun observeByStatus(status: IdeaStatus): Flow<List<Idea>> =
        ideaDao.observeByStatus(status).map(IdeaEntityMapper::toDomainList)

    override suspend fun getById(id: String): Idea? =
        ideaDao.getById(id)?.let(IdeaEntityMapper::toDomain)

    override suspend fun countByStatus(status: IdeaStatus): Int =
        ideaDao.countByStatus(status)

    override suspend fun insert(idea: Idea) {
        ideaDao.insert(IdeaEntityMapper.toEntity(idea))
    }

    override suspend fun update(idea: Idea) {
        ideaDao.update(IdeaEntityMapper.toEntity(idea))
    }

    override suspend fun updateStatus(id: String, status: IdeaStatus) {
        ideaDao.updateStatus(id, status)
    }

    override suspend fun delete(id: String) {
        val entity = ideaDao.getById(id) ?: return
        ideaDao.delete(entity)
    }
}
