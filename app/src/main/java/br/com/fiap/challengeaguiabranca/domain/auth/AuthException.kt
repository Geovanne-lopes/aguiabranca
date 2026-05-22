package br.com.fiap.challengeaguiabranca.domain.auth

sealed class AuthException(message: String) : Exception(message) {
    class UnauthorizedEmail : AuthException("E-mail não autorizado para acesso.")
    class InvalidPassword : AuthException("Senha incorreta para este usuário.")
    class UserDataUnavailable : AuthException("Não foi possível carregar os dados do usuário. Verifique a internet.")
}
