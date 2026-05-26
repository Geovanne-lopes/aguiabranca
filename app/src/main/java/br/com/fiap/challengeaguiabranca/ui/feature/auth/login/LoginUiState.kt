package br.com.fiap.challengeaguiabranca.ui.feature.auth.login

import br.com.fiap.challengeaguiabranca.domain.model.UserRole

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val mode: LoginMode = LoginMode.LOGIN,
    val registerName: String = "",
    val registerEmail: String = "",
    val registerPassword: String = "",
    val registerRole: UserRole = UserRole.OPERATOR,
    val resetEmail: String = "",
    val resetPassword: String = "",
    val successMessage: String? = null
)

enum class LoginMode {
    LOGIN,
    ABOUT,
    REGISTER,
    RESET
}
