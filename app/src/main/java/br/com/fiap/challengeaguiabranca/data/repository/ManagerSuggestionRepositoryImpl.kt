package br.com.fiap.challengeaguiabranca.data.repository

import br.com.fiap.challengeaguiabranca.data.local.datastore.ManagerSuggestionsDataStore
import br.com.fiap.challengeaguiabranca.domain.model.ManagerSuggestion
import br.com.fiap.challengeaguiabranca.domain.repository.ManagerSuggestionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ManagerSuggestionRepositoryImpl(
    private val dataStore: ManagerSuggestionsDataStore
) : ManagerSuggestionRepository {

    override fun observeAll(): Flow<List<ManagerSuggestion>> = dataStore.observeAll()

    override fun observeForTarget(email: String, authorId: String): Flow<List<ManagerSuggestion>> =
        dataStore.observeAll().map { list ->
            list.filter {
                it.targetEmail.equals(email, ignoreCase = true) ||
                    it.targetAuthorId == authorId
            }
        }

    override suspend fun insert(suggestion: ManagerSuggestion) {
        dataStore.insert(suggestion)
    }
}
