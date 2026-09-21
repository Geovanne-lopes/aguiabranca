@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package br.com.fiap.challengeaguiabranca.data.repository

import br.com.fiap.challengeaguiabranca.data.remote.ApiCaller
import br.com.fiap.challengeaguiabranca.data.remote.RefreshBus
import br.com.fiap.challengeaguiabranca.data.remote.api.InnovationApi
import br.com.fiap.challengeaguiabranca.data.remote.mapper.toManagerNotification
import br.com.fiap.challengeaguiabranca.data.remote.mapper.toOperatorNotification
import br.com.fiap.challengeaguiabranca.domain.model.ManagerNotification
import br.com.fiap.challengeaguiabranca.domain.model.OperatorNotification
import br.com.fiap.challengeaguiabranca.domain.repository.NotificationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import java.util.logging.Logger

class NotificationRemoteRepository(
    private val api: InnovationApi,
    private val apiCaller: ApiCaller,
    private val refreshBus: RefreshBus = RefreshBus()
) : NotificationRepository {

    override fun observeForOperator(): Flow<List<OperatorNotification>> =
        items().map { list -> list.mapNotNull { it.toOperatorNotification() } }

    override fun observeForManagement(): Flow<List<ManagerNotification>> =
        items().map { list -> list.map { it.toManagerNotification() } }

    private fun items() =
        refreshBus.updates()
            .flatMapLatest {
                flow {
                    emit(apiCaller.execute { api.notifications() }.items)
                }.catch { error ->
                    Logger.getLogger("NotificationRemote").warning("list failed: ${error.message}")
                    emit(emptyList())
                }
            }
            .flowOn(Dispatchers.IO)
}
