package br.com.fiap.challengeaguiabranca.ui.feature.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.fiap.challengeaguiabranca.domain.auth.AuthException
import br.com.fiap.challengeaguiabranca.domain.auth.AuthCatalog
import br.com.fiap.challengeaguiabranca.domain.model.UserRole
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

    fun openAbout() {
        _uiState.update { it.copy(mode = LoginMode.ABOUT, errorMessage = null, successMessage = null) }
    }

    fun openRegister() {
        _uiState.update { it.copy(mode = LoginMode.REGISTER, errorMessage = null, successMessage = null) }
    }

    fun openReset() {
        _uiState.update {
            it.copy(
                mode = LoginMode.RESET,
                resetEmail = it.email,
                errorMessage = null,
                successMessage = null
            )
        }
    }

    fun backToLogin() {
        _uiState.update { it.copy(mode = LoginMode.LOGIN, errorMessage = null) }
    }

    fun onRegisterNameChange(value: String) {
        _uiState.update { it.copy(registerName = value, errorMessage = null) }
    }

    fun onRegisterEmailChange(value: String) {
        _uiState.update { it.copy(registerEmail = value, errorMessage = null) }
    }

    fun onRegisterPasswordChange(value: String) {
        _uiState.update { it.copy(registerPassword = value, errorMessage = null) }
    }

    fun onRegisterRoleChange(value: UserRole) {
        _uiState.update { it.copy(registerRole = value) }
    }

    fun registerLocalAccount() {
        val state = _uiState.value
        when {
            state.registerName.length < 2 -> _uiState.update { it.copy(errorMessage = "Informe seu nome.") }
            !state.registerEmail.contains("@") -> _uiState.update { it.copy(errorMessage = "Informe um e-mail válido.") }
            state.registerPassword.length < 4 -> _uiState.update { it.copy(errorMessage = "Use uma senha com pelo menos 4 caracteres.") }
            !AuthCatalog.register(state.registerEmail, state.registerPassword, state.registerRole) ->
                _uiState.update { it.copy(errorMessage = "Já existe uma conta com este e-mail.") }
            else -> _uiState.update {
                it.copy(
                    mode = LoginMode.LOGIN,
                    email = state.registerEmail,
                    password = "",
                    successMessage = "Cadastro criado. Entre com o e-mail e senha que você acabou de criar.",
                    errorMessage = null
                )
            }
        }
    }

    fun onResetEmailChange(value: String) {
        _uiState.update { it.copy(resetEmail = value, errorMessage = null) }
    }

    fun onResetPasswordChange(value: String) {
        _uiState.update { it.copy(resetPassword = value, errorMessage = null) }
    }

    fun resetLocalPassword() {
        val state = _uiState.value
        when {
            !state.resetEmail.contains("@") -> _uiState.update { it.copy(errorMessage = "Informe o e-mail cadastrado.") }
            state.resetPassword.length < 4 -> _uiState.update { it.copy(errorMessage = "Use uma senha com pelo menos 4 caracteres.") }
            !AuthCatalog.resetPassword(state.resetEmail, state.resetPassword) ->
                _uiState.update { it.copy(errorMessage = "Não encontramos esse e-mail cadastrado.") }
            else -> _uiState.update {
                it.copy(
                    mode = LoginMode.LOGIN,
                    email = state.resetEmail,
                    password = "",
                    successMessage = "Senha alterada para ${state.resetEmail}. Faça login com a nova senha.",
                    errorMessage = null
                )
            }
        }
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
