package br.com.fiap.challengeaguiabranca.data.remote

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

class RefreshBus {
    private val ticks = MutableSharedFlow<Unit>(replay = 1, extraBufferCapacity = 64)

    init {
        ticks.tryEmit(Unit)
    }

    fun updates(): SharedFlow<Unit> = ticks

    fun refresh() {
        ticks.tryEmit(Unit)
    }
}
