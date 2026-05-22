package br.com.fiap.challengeaguiabranca.data.repository

import br.com.fiap.challengeaguiabranca.data.local.dao.ProjectDao
import br.com.fiap.challengeaguiabranca.data.local.mapper.ProjectEntityMapper
import br.com.fiap.challengeaguiabranca.domain.model.Project
import br.com.fiap.challengeaguiabranca.domain.model.ProjectStatus
import br.com.fiap.challengeaguiabranca.domain.repository.ProjectRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ProjectRepositoryImpl(
    private val projectDao: ProjectDao
) : ProjectRepository {

    override fun observeAll(): Flow<List<Project>> =
        projectDao.observeAll().map(ProjectEntityMapper::toDomainList)

    override fun observeByStatus(status: ProjectStatus): Flow<List<Project>> =
        projectDao.observeByStatus(status).map(ProjectEntityMapper::toDomainList)

    override suspend fun getById(id: String): Project? =
        projectDao.getById(id)?.let(ProjectEntityMapper::toDomain)

    override suspend fun getByIdeaId(ideaId: String): Project? =
        projectDao.getByIdeaId(ideaId)?.let(ProjectEntityMapper::toDomain)

    override suspend fun insert(project: Project) {
        projectDao.insert(ProjectEntityMapper.toEntity(project))
    }

    override suspend fun update(project: Project) {
        projectDao.update(ProjectEntityMapper.toEntity(project))
    }

    override suspend fun delete(id: String) {
        val entity = projectDao.getById(id) ?: return
        projectDao.delete(entity)
    }
}
