package br.com.fiap.challengeaguiabranca.data.remote.auth

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class AuthInterceptorTest {

    private val server = MockWebServer()

    @Before
    fun setUp() {
        server.start()
    }

    @After
    fun tearDown() {
        server.shutdown()
    }

    @Test
    fun addsBearerWhenAccessTokenExists() {
        server.enqueue(MockResponse().setResponseCode(200))
        val client = client(token = "access-1")

        client.newCall(
            Request.Builder().url(server.url("/api/v1/ideas")).build()
        ).execute().close()

        assertEquals("Bearer access-1", server.takeRequest().getHeader("Authorization"))
    }

    @Test
    fun doesNotAddBearerOnLogin() {
        server.enqueue(MockResponse().setResponseCode(200))
        val client = client(token = "access-1")

        client.newCall(
            Request.Builder().url(server.url("/api/v1/auth/login")).build()
        ).execute().close()

        assertNull(server.takeRequest().getHeader("Authorization"))
    }

    private fun client(token: String?): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor { token })
            .build()
    }
}
