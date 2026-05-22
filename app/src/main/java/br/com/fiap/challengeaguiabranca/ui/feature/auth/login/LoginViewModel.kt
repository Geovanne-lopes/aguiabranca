package br.com.fiap.challengeaguiabranca.ui.feature.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.fiap.challengeaguiabranca.domain.auth.AuthException
import br.com.fiap.challengeaguiabranca.domain.usecase.auth.AuthenticateUserUseCase
import br.com.fiap.challengeaguiabranca.ui.navigation.Routes
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authenticateUserUseCase: AuthenticateUserUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState = _uiState.asStateFlow()

    private val _events = Channel<LoginEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    fun onEmailChange(value: String) {
        _uiState.update { it.copy(email = value, errorMessage = null) }
    }

    fun onPasswordChange(value: String) {
        _uiState.update { it.copy(password = value, errorMessage = null) }
    }

    fun onLoginClick() {
        val state = _uiState.value
        when {
            state.email.isBlank() || !state.email.contains("@") ->
                _uiState.update { it.copy(errorMessage = "Informe um e-mail válido.") }
            state.password.isBlank() ->
                _uiState.update { it.copy(errorMessage = "Informe sua senha.") }
            else -> viewModelScope.launch {
                _uiState.update { it.copy(isLoading = true, errorMessage = null) }
                runCatching {
                    authenticateUserUseCase(
                        email = state.email,
                        password = state.password
                    )
                }.onSuccess { user ->
                    _uiState.update { it.copy(isLoading = false) }
                    _events.send(
                        LoginEvent.NavigateToHome(Routes.homeForRole(user.role))
                    )
                }.onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = mapAuthError(error)
                        )
                    }
                }
            }
        }
    }

    private fun mapAuthError(error: Throwable): String = when (error) {
        is AuthException.UnauthorizedEmail ->
            "E-mail não autorizado. Use a conta do seu perfil (Operador, Gestor ou Liderança)."
        is AuthException.InvalidPassword -> "Senha incorreta para este e-mail."
        is AuthException.UserDataUnavailable ->
            "Não foi possível validar o usuário. Verifique sua conexão e tente novamente."
        else -> error.message ?: "Erro ao entrar. Tente novamente."
    }
}

sealed interface LoginEvent {
    data class NavigateToHome(val route: String) : LoginEvent
}
