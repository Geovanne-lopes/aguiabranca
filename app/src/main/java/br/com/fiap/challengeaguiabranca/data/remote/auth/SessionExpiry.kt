package br.com.fiap.challengeaguiabranca.data.remote.auth

import br.com.fiap.challengeaguiabranca.data.local.datastore.TokenStore
import br.com.fiap.challengeaguiabranca.domain.repository.SessionRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

class SessionExpiry(
    private val tokenStore: TokenStore,
    private val sessionRepository: SessionRepository
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val _events = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val events: SharedFlow<Unit> = _events

    fun expire() {
        tokenStore.clearMemory()
        scope.launch {
            runCatching { tokenStore.clear() }
            runCatching { sessionRepository.clearSession() }
            _events.emit(Unit)
        }
    }
}
