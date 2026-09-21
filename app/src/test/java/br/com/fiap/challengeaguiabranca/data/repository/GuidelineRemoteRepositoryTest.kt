package br.com.fiap.challengeaguiabranca.data.repository

import br.com.fiap.challengeaguiabranca.data.remote.ApiCaller
import br.com.fiap.challengeaguiabranca.data.remote.api.InnovationApi
import br.com.fiap.challengeaguiabranca.domain.error.RemoteCallException
import br.com.fiap.challengeaguiabranca.domain.model.StrategicGuideline
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

class GuidelineRemoteRepositoryTest {

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
    fun updateByOperatorMapsForbiddenToPermissionMessage() = runBlocking {
        server.enqueue(
            MockResponse()
                .setResponseCode(403)
                .setHeader("Content-Type", "application/json")
                .setBody(
                    """
                    {
                      "status": 403,
                      "code": "FORBIDDEN",
                      "message": "Perfil não autorizado para este recurso.",
                      "details": []
                    }
                    """.trimIndent()
                )
        )

        val error = runCatching { repository().update(sampleGuideline()) }.exceptionOrNull()

        check(error is RemoteCallException.Forbidden)
        assertEquals("Sem permissão para esta ação.", error.message)
    }

    private fun repository(): GuidelineRemoteRepository {
        val json = Json { ignoreUnknownKeys = true }
        val api = Retrofit.Builder()
            .baseUrl(server.url("/"))
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(InnovationApi::class.java)
        return GuidelineRemoteRepository(api, ApiCaller(json))
    }

    private fun sampleGuideline() = StrategicGuideline(
        id = "gl-1",
        title = "Sustentabilidade",
        content = "Incentivar ideias com impacto ambiental mensurável.",
        authorId = "leader-1"
    )
}
