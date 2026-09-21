@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package br.com.fiap.challengeaguiabranca.data.repository

import br.com.fiap.challengeaguiabranca.data.remote.ApiCaller
import br.com.fiap.challengeaguiabranca.data.remote.RefreshBus
import br.com.fiap.challengeaguiabranca.data.remote.api.InnovationApi
import br.com.fiap.challengeaguiabranca.data.remote.dto.UpdateIdeaStatusRequestDto
import br.com.fiap.challengeaguiabranca.data.remote.mapper.toCreateRequest
import br.com.fiap.challengeaguiabranca.data.remote.mapper.toDomain
import br.com.fiap.challengeaguiabranca.domain.error.RemoteCallException
import br.com.fiap.challengeaguiabranca.domain.model.Idea
import br.com.fiap.challengeaguiabranca.domain.model.IdeaStatus
import br.com.fiap.challengeaguiabranca.domain.repository.IdeaRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.util.logging.Logger

class IdeaRemoteRepository(
    private val api: InnovationApi,
    private val apiCaller: ApiCaller,
    private val refreshBus: RefreshBus = RefreshBus()
) : IdeaRepository {

    override fun observeAll(): Flow<List<Idea>> = observe(status = null, authorId = null)

    override fun observeByAuthor(authorId: String): Flow<List<Idea>> =
        observe(status = null, authorId = authorId).let { source ->
            source
        }

    override fun observeByStatus(status: IdeaStatus): Flow<List<Idea>> =
        observe(status = status.name, authorId = null)

    override suspend fun getById(id: String): Idea? =
        try {
            apiCaller.execute { api.getIdea(id) }.toDomain()
        } catch (error: RemoteCallException.NotFound) {
            null
        }

    override suspend fun countByStatus(status: IdeaStatus): Int {
        val page = apiCaller.execute { api.listIdeas(status = status.name) }
        apiCaller.warnIfTruncated("ideas", page.totalElements, page.content.size)
        return page.totalElements.toInt()
    }

    override suspend fun insert(idea: Idea) {
        apiCaller.execute { api.createIdea(idea.toCreateRequest()) }
        refreshBus.refresh()
    }

    override suspend fun update(idea: Idea) {
        apiCaller.execute { api.updateIdea(idea.id, idea.toCreateRequest()) }
        refreshBus.refresh()
    }

    override suspend fun updateStatus(id: String, status: IdeaStatus, justification: String?) {
        apiCaller.execute {
            api.updateIdeaStatus(
                id,
                UpdateIdeaStatusRequestDto(status = status.name, justification = justification)
            )
        }
        refreshBus.refresh()
    }

    override suspend fun delete(id: String) {
        apiCaller.executeEmpty { api.deleteIdea(id) }
        refreshBus.refresh()
    }

    private fun observe(status: String?, authorId: String?): Flow<List<Idea>> =
        refreshBus.updates()
            .flatMapLatest {
                flow {
                    val page = apiCaller.execute {
                        api.listIdeas(status = status, authorId = authorId)
                    }
                    apiCaller.warnIfTruncated("ideas", page.totalElements, page.content.size)
                    val ideas = page.content.map { it.toDomain() }
                    emit(
                        if (authorId.isNullOrBlank()) ideas
                        else ideas.filter { it.authorId == authorId }
                    )
                }.catch { error ->
                    Logger.getLogger("IdeaRemote").warning("list failed: ${error.message}")
                    emit(emptyList())
                }
            }
            .flowOn(Dispatchers.IO)
}
