package br.com.fiap.challengeaguiabranca.data.remote.auth

import okhttp3.Interceptor
import okhttp3.Response

fun interface AccessTokenSource {
    fun currentAccessToken(): String?
}

class AuthInterceptor(
    private val tokens: AccessTokenSource
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        if (isPublicAuth(request.url.encodedPath)) {
            return chain.proceed(request)
        }
        val token = tokens.currentAccessToken()
        if (token.isNullOrBlank()) {
            return chain.proceed(request)
        }
        val authed = request.newBuilder()
            .header("Authorization", "Bearer $token")
            .build()
        return chain.proceed(authed)
    }

    companion object {
        fun isPublicAuth(path: String): Boolean {
            return path.contains("/api/v1/auth/login") ||
                path.contains("/api/v1/auth/register") ||
                path.contains("/api/v1/auth/refresh") ||
                path.contains("/api/v1/auth/reset-password")
        }
    }
}
