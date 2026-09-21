@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package br.com.fiap.challengeaguiabranca.data.repository

import br.com.fiap.challengeaguiabranca.data.remote.ApiCaller
import br.com.fiap.challengeaguiabranca.data.remote.RefreshBus
import br.com.fiap.challengeaguiabranca.data.remote.api.InnovationApi
import br.com.fiap.challengeaguiabranca.data.remote.dto.CreateSuggestionRequestDto
import br.com.fiap.challengeaguiabranca.data.remote.mapper.toDomain
import br.com.fiap.challengeaguiabranca.domain.model.ManagerSuggestion
import br.com.fiap.challengeaguiabranca.domain.repository.ManagerSuggestionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import java.util.logging.Logger

class SuggestionRemoteRepository(
    private val api: InnovationApi,
    private val apiCaller: ApiCaller,
    private val refreshBus: RefreshBus = RefreshBus()
) : ManagerSuggestionRepository {

    override fun observeAll(): Flow<List<ManagerSuggestion>> = observe()

    override fun observeForTarget(email: String, authorId: String): Flow<List<ManagerSuggestion>> =
        observe().map { suggestions ->
            suggestions.filter { suggestion ->
                suggestion.targetAuthorId == authorId ||
                    suggestion.targetEmail.equals(email, ignoreCase = true)
            }
        }

    override suspend fun insert(suggestion: ManagerSuggestion) {
        apiCaller.execute {
            api.createSuggestion(
                CreateSuggestionRequestDto(
                    targetUserId = suggestion.targetAuthorId,
                    message = suggestion.message
                )
            )
        }
        refreshBus.refresh()
    }

    private fun observe(): Flow<List<ManagerSuggestion>> =
        refreshBus.updates()
            .flatMapLatest {
                flow {
                    val page = apiCaller.execute { api.listSuggestions() }
                    apiCaller.warnIfTruncated("suggestions", page.totalElements, page.content.size)
                    emit(page.content.map { it.toDomain() })
                }.catch { error ->
                    Logger.getLogger("SuggestionRemote").warning("list failed: ${error.message}")
                    emit(emptyList())
                }
            }
            .flowOn(Dispatchers.IO)
}
