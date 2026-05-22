package br.com.fiap.challengeaguiabranca.domain.repository

import br.com.fiap.challengeaguiabranca.domain.model.Idea
import br.com.fiap.challengeaguiabranca.domain.model.IdeaStatus
import kotlinx.coroutines.flow.Flow

interface IdeaRepository {
    fun observeAll(): Flow<List<Idea>>
    fun observeByAuthor(authorId: String): Flow<List<Idea>>
    fun observeByStatus(status: IdeaStatus): Flow<List<Idea>>
    suspend fun getById(id: String): Idea?
    suspend fun countByStatus(status: IdeaStatus): Int
    suspend fun insert(idea: Idea)
    suspend fun update(idea: Idea)
    suspend fun updateStatus(id: String, status: IdeaStatus)
    suspend fun delete(id: String)
}
