package br.com.fiap.challengeaguiabranca.ui.feature.auth.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.fiap.challengeaguiabranca.domain.model.User
import br.com.fiap.challengeaguiabranca.domain.model.UserRole
import br.com.fiap.challengeaguiabranca.domain.usecase.auth.FetchSeedUsersUseCase
import br.com.fiap.challengeaguiabranca.domain.usecase.session.SaveUserSessionUseCase
import br.com.fiap.challengeaguiabranca.ui.navigation.Routes
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileSelectionViewModel(
    private val fetchSeedUsersUseCase: FetchSeedUsersUseCase,
    private val saveUserSessionUseCase: SaveUserSessionUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileSelectionUiState())
    val uiState = _uiState.asStateFlow()

    private val _events = Channel<ProfileSelectionEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    init {
        loadUsers()
    }

    fun loadUsers() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            runCatching { fetchSeedUsersUseCase() }
                .onSuccess { users ->
                    _uiState.update {
                        it.copy(isLoading = false, users = users, errorMessage = null)
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "Erro ao carregar perfis."
                        )
                    }
                }
        }
    }

    fun onProfileSelected(role: UserRole) {
        val user = _uiState.value.userForRole(role)
        if (user == null) {
            _uiState.update { it.copy(errorMessage = "Perfil indisponível. Tente recarregar.") }
            return
        }
        saveSessionAndNavigate(user)
    }

    private fun saveSessionAndNavigate(user: User) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null) }
            runCatching { saveUserSessionUseCase(user) }
                .onSuccess {
                    _uiState.update { it.copy(isSaving = false) }
                    _events.send(ProfileSelectionEvent.NavigateToHome(Routes.homeForRole(user.role)))
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isSaving = false,
                            errorMessage = error.message ?: "Erro ao salvar sessão."
                        )
                    }
                }
        }
    }
}

sealed interface ProfileSelectionEvent {
    data class NavigateToHome(val route: String) : ProfileSelectionEvent
}
