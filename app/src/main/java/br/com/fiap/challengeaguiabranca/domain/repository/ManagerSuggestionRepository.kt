package br.com.fiap.challengeaguiabranca.domain.repository

import br.com.fiap.challengeaguiabranca.domain.model.ManagerSuggestion
import kotlinx.coroutines.flow.Flow

interface ManagerSuggestionRepository {
    fun observeAll(): Flow<List<ManagerSuggestion>>
    fun observeForTarget(email: String, authorId: String): Flow<List<ManagerSuggestion>>
    suspend fun insert(suggestion: ManagerSuggestion)
}
