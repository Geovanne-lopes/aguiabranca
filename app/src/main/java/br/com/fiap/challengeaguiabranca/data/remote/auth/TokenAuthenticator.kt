package br.com.fiap.challengeaguiabranca.data.remote.auth

import br.com.fiap.challengeaguiabranca.data.local.datastore.TokenStore
import br.com.fiap.challengeaguiabranca.data.remote.api.InnovationApi
import br.com.fiap.challengeaguiabranca.data.remote.dto.RefreshRequestDto
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

class TokenAuthenticator(
    private val tokenStore: TokenStore,
    private val publicApi: InnovationApi,
    private val sessionExpiry: SessionExpiry
) : Authenticator {

    private val mutex = Mutex()

    override fun authenticate(route: Route?, response: Response): Request? {
        if (responseCount(response) >= 2) {
            sessionExpiry.expire()
            return null
        }
        val path = response.request.url.encodedPath
        if (AuthInterceptor.isPublicAuth(path)) return null

        return runBlocking {
            mutex.withLock {
                val latest = tokenStore.currentAccessToken()
                val used = response.request.header("Authorization")
                    ?.removePrefix("Bearer ")
                    ?.trim()
                if (!latest.isNullOrBlank() && latest != used) {
                    return@withLock response.request.newBuilder()
                        .header("Authorization", "Bearer $latest")
                        .build()
                }
                val refreshToken = tokenStore.currentRefreshToken()
                if (refreshToken.isNullOrBlank()) {
                    sessionExpiry.expire()
                    return@withLock null
                }
                val refreshed = runCatching {
                    publicApi.refresh(RefreshRequestDto(refreshToken))
                }.getOrNull()
                val body = refreshed?.body()
                if (refreshed == null || !refreshed.isSuccessful || body == null) {
                    sessionExpiry.expire()
                    null
                } else {
                    tokenStore.save(body.accessToken, body.refreshToken)
                    response.request.newBuilder()
                        .header("Authorization", "Bearer ${body.accessToken}")
                        .build()
                }
            }
        }
    }

    private fun responseCount(response: Response): Int {
        var count = 1
        var prior = response.priorResponse
        while (prior != null) {
            count++
            prior = prior.priorResponse
        }
        return count
    }
}
