package br.com.fiap.challengeaguiabranca.ui.feature.manager.suggestion

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.fiap.challengeaguiabranca.domain.catalog.MockOperatorsCatalog
import br.com.fiap.challengeaguiabranca.domain.usecase.manager.SendManagerSuggestionUseCase
import br.com.fiap.challengeaguiabranca.domain.usecase.session.ObserveCurrentUserUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ManagerSuggestionUiState(
    val managerName: String = "",
    val operators: List<OperatorOption> = emptyList(),
    val selectedOperatorId: String = "",
    val message: String = "",
    val isSending: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

data class OperatorOption(
    val id: String,
    val name: String,
    val email: String
)

class ManagerSuggestionViewModel(
    observeCurrentUserUseCase: ObserveCurrentUserUseCase,
    private val sendManagerSuggestionUseCase: SendManagerSuggestionUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ManagerSuggestionUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            observeCurrentUserUseCase().collect { user ->
                _uiState.update {
                    it.copy(
                        managerName = user?.name ?: "",
                        operators = MockOperatorsCatalog.operators.map { op ->
                            OperatorOption(op.id, op.name, op.email)
                        },
                        selectedOperatorId = it.selectedOperatorId.ifBlank {
                            MockOperatorsCatalog.operators.firstOrNull()?.id ?: ""
                        }
                    )
                }
            }
        }
    }

    fun onOperatorSelected(id: String) {
        _uiState.update { it.copy(selectedOperatorId = id, errorMessage = null) }
    }

    fun onMessageChange(value: String) {
        _uiState.update { it.copy(message = value, errorMessage = null) }
    }

    fun sendSuggestion() {
        val state = _uiState.value
        val target = state.operators.find { it.id == state.selectedOperatorId }
        if (target == null) {
            _uiState.update { it.copy(errorMessage = "Selecione um operador.") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isSending = true, errorMessage = null) }
            sendManagerSuggestionUseCase(
                managerName = state.managerName,
                targetAuthorId = target.id,
                targetEmail = target.email,
                targetName = target.name,
                message = state.message
            ).onSuccess {
                _uiState.update {
                    it.copy(
                        isSending = false,
                        message = "",
                        successMessage = "Sugestão enviada para ${target.name}!"
                    )
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(isSending = false, errorMessage = error.message ?: "Erro ao enviar.")
                }
            }
        }
    }

    fun clearSuccessMessage() {
        _uiState.update { it.copy(successMessage = null) }
    }
}
