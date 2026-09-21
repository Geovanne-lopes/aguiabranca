@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package br.com.fiap.challengeaguiabranca.data.repository

import br.com.fiap.challengeaguiabranca.data.remote.ApiCaller
import br.com.fiap.challengeaguiabranca.data.remote.RefreshBus
import br.com.fiap.challengeaguiabranca.data.remote.api.InnovationApi
import br.com.fiap.challengeaguiabranca.data.remote.dto.CreateProjectRequestDto
import br.com.fiap.challengeaguiabranca.data.remote.mapper.toDomain
import br.com.fiap.challengeaguiabranca.data.remote.mapper.toUpdateRequest
import br.com.fiap.challengeaguiabranca.domain.error.RemoteCallException
import br.com.fiap.challengeaguiabranca.domain.model.Project
import br.com.fiap.challengeaguiabranca.domain.model.ProjectStatus
import br.com.fiap.challengeaguiabranca.domain.repository.ProjectRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.util.logging.Logger

class ProjectRemoteRepository(
    private val api: InnovationApi,
    private val apiCaller: ApiCaller,
    private val refreshBus: RefreshBus = RefreshBus()
) : ProjectRepository {

    override fun observeAll(): Flow<List<Project>> = observe(status = null)

    override fun observeByStatus(status: ProjectStatus): Flow<List<Project>> =
        observe(status = status.name)

    override suspend fun getById(id: String): Project? =
        try {
            apiCaller.execute { api.getProject(id) }.toDomain()
        } catch (error: RemoteCallException.NotFound) {
            null
        }

    override suspend fun getByIdeaId(ideaId: String): Project? =
        try {
            apiCaller.execute { api.getProjectByIdea(ideaId) }.toDomain()
        } catch (error: RemoteCallException.NotFound) {
            null
        }

    override suspend fun insert(project: Project) {
        createFromIdea(project.ideaId)
    }

    override suspend fun createFromIdea(ideaId: String): Project {
        val created = try {
            apiCaller.execute { api.createProject(CreateProjectRequestDto(ideaId)) }.toDomain()
        } catch (error: RemoteCallException.Conflict) {
            getByIdeaId(ideaId) ?: throw error
        }
        refreshBus.refresh()
        return created
    }

    override suspend fun update(project: Project) {
        apiCaller.execute { api.updateProject(project.id, project.toUpdateRequest()) }
        refreshBus.refresh()
    }

    override suspend fun delete(id: String) {
        apiCaller.executeEmpty { api.deleteProject(id) }
        refreshBus.refresh()
    }

    private fun observe(status: String?): Flow<List<Project>> =
        refreshBus.updates()
            .flatMapLatest {
                flow {
                    val page = apiCaller.execute { api.listProjects(status = status) }
                    apiCaller.warnIfTruncated("projects", page.totalElements, page.content.size)
                    emit(page.content.map { it.toDomain() })
                }.catch { error ->
                    Logger.getLogger("ProjectRemote").warning("list failed: ${error.message}")
                    emit(emptyList())
                }
            }
            .flowOn(Dispatchers.IO)
}
