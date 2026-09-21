package br.com.fiap.challengeaguiabranca.data.repository

import br.com.fiap.challengeaguiabranca.data.local.datastore.TokenStore
import br.com.fiap.challengeaguiabranca.data.remote.ApiCaller
import br.com.fiap.challengeaguiabranca.data.remote.api.InnovationApi
import br.com.fiap.challengeaguiabranca.data.remote.dto.LoginRequestDto
import br.com.fiap.challengeaguiabranca.data.remote.dto.LogoutRequestDto
import br.com.fiap.challengeaguiabranca.data.remote.dto.RegisterRequestDto
import br.com.fiap.challengeaguiabranca.data.remote.dto.ResetPasswordRequestDto
import br.com.fiap.challengeaguiabranca.data.remote.mapper.toDomain
import br.com.fiap.challengeaguiabranca.domain.auth.AuthException
import br.com.fiap.challengeaguiabranca.domain.error.RemoteCallException
import br.com.fiap.challengeaguiabranca.domain.model.User
import br.com.fiap.challengeaguiabranca.domain.model.UserRole
import br.com.fiap.challengeaguiabranca.domain.repository.AuthRepository

class AuthRemoteRepository(
    private val publicApi: InnovationApi,
    private val securedApi: InnovationApi,
    private val apiCaller: ApiCaller,
    private val tokenStore: TokenStore
) : AuthRepository {

    override suspend fun login(email: String, password: String): User {
        try {
            val response = apiCaller.execute {
                publicApi.login(LoginRequestDto(email = email, password = password))
            }
            tokenStore.save(response.accessToken, response.refreshToken)
            return response.user.toDomain()
        } catch (error: RemoteCallException.Unauthorized) {
            throw AuthException.InvalidCredentials()
        } catch (error: RemoteCallException.Network) {
            throw AuthException.UserDataUnavailable()
        }
    }

    override suspend fun register(name: String, email: String, password: String, role: UserRole) {
        apiCaller.execute {
            publicApi.register(
                RegisterRequestDto(
                    name = name,
                    email = email,
                    password = password,
                    role = role.name
                )
            )
        }
    }

    override suspend fun resetPassword(email: String, newPassword: String) {
        apiCaller.executeEmpty {
            publicApi.resetPassword(ResetPasswordRequestDto(email = email, newPassword = newPassword))
        }
    }

    override suspend fun logout() {
        val refresh = tokenStore.currentRefreshToken()
        if (!tokenStore.currentAccessToken().isNullOrBlank() || !refresh.isNullOrBlank()) {
            runCatching {
                apiCaller.executeEmpty {
                    securedApi.logout(LogoutRequestDto(refreshToken = refresh))
                }
            }
        }
        tokenStore.clear()
    }

    override suspend fun restoreSession(): User? {
        tokenStore.warmUp()
        if (tokenStore.currentAccessToken().isNullOrBlank() && tokenStore.currentRefreshToken().isNullOrBlank()) {
            return null
        }
        return try {
            apiCaller.execute { securedApi.me() }.toDomain()
        } catch (error: RemoteCallException.Unauthorized) {
            tokenStore.clear()
            null
        } catch (error: RemoteCallException.Network) {
            null
        }
    }
}
