package br.com.fiap.challengeaguiabranca.domain.repository

import br.com.fiap.challengeaguiabranca.domain.model.Project
import br.com.fiap.challengeaguiabranca.domain.model.ProjectStatus
import kotlinx.coroutines.flow.Flow

interface ProjectRepository {
    fun observeAll(): Flow<List<Project>>
    fun observeByStatus(status: ProjectStatus): Flow<List<Project>>
    suspend fun getById(id: String): Project?
    suspend fun getByIdeaId(ideaId: String): Project?
    suspend fun insert(project: Project)
    suspend fun update(project: Project)
    suspend fun delete(id: String)
}
