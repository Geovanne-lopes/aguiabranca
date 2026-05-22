package br.com.fiap.challengeaguiabranca.ui.feature.shared

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.fiap.challengeaguiabranca.domain.usecase.session.ClearSessionUseCase
import br.com.fiap.challengeaguiabranca.domain.usecase.session.ObserveCurrentUserUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class RoleHomeViewModel(
    observeCurrentUserUseCase: ObserveCurrentUserUseCase,
    private val clearSessionUseCase: ClearSessionUseCase
) : ViewModel() {

    val userName = observeCurrentUserUseCase()
        .map { user -> user?.name?.substringBefore(" ") }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    fun logout(onCompleted: () -> Unit) {
        viewModelScope.launch {
            clearSessionUseCase()
            onCompleted()
        }
    }
}
