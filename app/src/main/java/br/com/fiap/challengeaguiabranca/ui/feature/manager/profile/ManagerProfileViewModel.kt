package br.com.fiap.challengeaguiabranca.ui.feature.manager.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.fiap.challengeaguiabranca.domain.model.IdeaStatus
import br.com.fiap.challengeaguiabranca.domain.model.ProjectStatus
import br.com.fiap.challengeaguiabranca.domain.usecase.idea.ObserveAllIdeasUseCase
import br.com.fiap.challengeaguiabranca.domain.usecase.project.ObserveAllProjectsUseCase
import br.com.fiap.challengeaguiabranca.domain.usecase.session.ClearSessionUseCase
import br.com.fiap.challengeaguiabranca.domain.usecase.session.ObserveCurrentUserUseCase
import br.com.fiap.challengeaguiabranca.domain.usecase.session.UpdateUserProfileUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class ManagerProfileViewModel(
    observeCurrentUserUseCase: ObserveCurrentUserUseCase,
    observeAllIdeasUseCase: ObserveAllIdeasUseCase,
    observeAllProjectsUseCase: ObserveAllProjectsUseCase,
    private val updateUserProfileUseCase: UpdateUserProfileUseCase,
    private val clearSessionUseCase: ClearSessionUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ManagerProfileUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            observeCurrentUserUseCase()
                .flatMapLatest { user ->
                    if (user == null) {
                        flowOf(ManagerProfileUiState(isLoading = false))
                    } else {
                        combine(
                            observeAllIdeasUseCase(),
                            observeAllProjectsUseCase()
                        ) { ideas, projects ->
                            ManagerProfileUiState(
                                name = user.name,
                                email = user.email,
                                avatarUrl = user.avatarUrl,
                                editName = user.name,
                                editEmail = user.email,
                                pendingIdeas = ideas.count { it.status == IdeaStatus.PENDING },
                                activeProjects = projects.count { it.status != ProjectStatus.COMPLETED },
                                totalIdeas = ideas.size,
                                isLoading = false
                            )
                        }
                    }
                }
                .collect { state ->
                    _uiState.update { current ->
                        state.copy(
                            editName = if (current.isEditing) current.editName else state.name,
                            editEmail = if (current.isEditing) current.editEmail else state.email,
                            editAvatarUrl = current.editAvatarUrl ?: state.avatarUrl,
                            isEditing = current.isEditing,
                            isSaving = current.isSaving,
                            errorMessage = current.errorMessage,
                            successMessage = current.successMessage
                        )
                    }
                }
        }
    }

    fun startEditing() {
        _uiState.update {
            it.copy(isEditing = true, editName = it.name, editEmail = it.email, errorMessage = null)
        }
    }

    fun cancelEditing() {
        _uiState.update {
            it.copy(isEditing = false, editName = it.name, editEmail = it.email, errorMessage = null)
        }
    }

    fun onNameChange(value: String) = _uiState.update { it.copy(editName = value, errorMessage = null) }
    fun onEmailChange(value: String) = _uiState.update { it.copy(editEmail = value, errorMessage = null) }
    fun onAvatarSelected(url: String?) = _uiState.update { it.copy(avatarUrl = url, editAvatarUrl = url) }

    fun saveProfile() {
        val state = _uiState.value
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null) }
            updateUserProfileUseCase(state.editName, state.editEmail, state.editAvatarUrl ?: state.avatarUrl)
                .onSuccess {
                    _uiState.update { it.copy(isSaving = false, isEditing = false, successMessage = "Perfil atualizado!") }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isSaving = false, errorMessage = e.message) }
                }
        }
    }

    fun clearSuccessMessage() = _uiState.update { it.copy(successMessage = null) }

    fun logout(onCompleted: () -> Unit) {
        viewModelScope.launch {
            clearSessionUseCase()
            onCompleted()
        }
    }
}

data class ManagerProfileUiState(
    val name: String = "",
    val email: String = "",
    val avatarUrl: String? = null,
    val editName: String = "",
    val editEmail: String = "",
    val editAvatarUrl: String? = null,
    val pendingIdeas: Int = 0,
    val activeProjects: Int = 0,
    val totalIdeas: Int = 0,
    val isEditing: Boolean = false,
    val isSaving: Boolean = false,
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val successMessage: String? = null
)
