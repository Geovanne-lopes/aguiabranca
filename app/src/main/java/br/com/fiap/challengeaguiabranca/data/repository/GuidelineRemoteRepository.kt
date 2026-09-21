@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package br.com.fiap.challengeaguiabranca.data.repository

import br.com.fiap.challengeaguiabranca.data.remote.ApiCaller
import br.com.fiap.challengeaguiabranca.data.remote.RefreshBus
import br.com.fiap.challengeaguiabranca.data.remote.api.InnovationApi
import br.com.fiap.challengeaguiabranca.data.remote.mapper.toDomain
import br.com.fiap.challengeaguiabranca.data.remote.mapper.toWriteRequest
import br.com.fiap.challengeaguiabranca.domain.error.RemoteCallException
import br.com.fiap.challengeaguiabranca.domain.model.GuidelineHistoryEntry
import br.com.fiap.challengeaguiabranca.domain.model.StrategicGuideline
import br.com.fiap.challengeaguiabranca.domain.repository.GuidelineRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.util.logging.Logger

class GuidelineRemoteRepository(
    private val api: InnovationApi,
    private val apiCaller: ApiCaller,
    private val refreshBus: RefreshBus = RefreshBus()
) : GuidelineRepository {

    override fun observeAll(): Flow<List<StrategicGuideline>> =
        refreshBus.updates()
            .flatMapLatest {
                flow {
                    val page = apiCaller.execute { api.listGuidelines() }
                    apiCaller.warnIfTruncated("guidelines", page.totalElements, page.content.size)
                    emit(page.content.map { it.toDomain() })
                }.catch { error ->
                    Logger.getLogger("GuidelineRemote").warning("list failed: ${error.message}")
                    emit(emptyList())
                }
            }
            .flowOn(Dispatchers.IO)

    override suspend fun getById(id: String): StrategicGuideline? =
        try {
            apiCaller.execute { api.getGuideline(id) }.toDomain()
        } catch (error: RemoteCallException.NotFound) {
            null
        }

    override suspend fun insert(guideline: StrategicGuideline) {
        apiCaller.execute { api.createGuideline(guideline.toWriteRequest()) }
        refreshBus.refresh()
    }

    override suspend fun update(guideline: StrategicGuideline) {
        apiCaller.execute { api.updateGuideline(guideline.id, guideline.toWriteRequest()) }
        refreshBus.refresh()
    }

    override suspend fun delete(id: String) {
        apiCaller.executeEmpty { api.deleteGuideline(id) }
        refreshBus.refresh()
    }

    override suspend fun history(guidelineId: String): List<GuidelineHistoryEntry> {
        val page = apiCaller.execute { api.guidelineHistory(guidelineId) }
        return page.content.map { it.toDomain() }
    }
}
