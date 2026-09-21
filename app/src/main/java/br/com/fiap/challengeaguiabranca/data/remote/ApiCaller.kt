package br.com.fiap.challengeaguiabranca.data.remote

import br.com.fiap.challengeaguiabranca.data.remote.dto.ApiErrorDto
import br.com.fiap.challengeaguiabranca.domain.error.RemoteCallException
import kotlinx.serialization.json.Json
import retrofit2.Response
import java.io.IOException
import java.util.logging.Logger

class ApiCaller(
    private val json: Json
) {
    suspend fun <T> execute(call: suspend () -> Response<T>): T {
        val response = invoke(call)
        if (response.isSuccessful) {
            return response.body() ?: throw RemoteCallException.Server()
        }
        throw map(response.code(), response.errorBody()?.string())
    }

    suspend fun executeEmpty(call: suspend () -> Response<Unit>) {
        val response = invoke(call)
        if (!response.isSuccessful) {
            throw map(response.code(), response.errorBody()?.string())
        }
    }

    private suspend fun <T> invoke(call: suspend () -> Response<T>): Response<T> =
        try {
            call()
        } catch (error: IOException) {
            throw RemoteCallException.Network()
        }

    fun map(status: Int, rawBody: String?): RemoteCallException {
        val parsed = rawBody?.let { body ->
            runCatching { json.decodeFromString<ApiErrorDto>(body) }.getOrNull()
        }
        val detail = parsed?.details?.firstOrNull()?.let { item ->
            listOfNotNull(item.field, item.issue).joinToString(": ").ifBlank { null }
        }
        val message = detail ?: parsed?.message
        val code = parsed?.code
        return when (status) {
            401 -> RemoteCallException.Unauthorized(message ?: "Sessão expirada. Entre novamente.")
            403 -> RemoteCallException.Forbidden()
            404 -> RemoteCallException.NotFound(message ?: "Registro não encontrado.")
            409 -> RemoteCallException.Conflict(
                message = message ?: "Conflito ao salvar.",
                code = code
            )
            400, 422 -> RemoteCallException.Validation(
                message ?: "Um ou mais campos são inválidos."
            )
            429 -> RemoteCallException.Validation(
                message ?: "Muitas tentativas. Aguarde e tente novamente."
            )
            else -> RemoteCallException.Server(
                message ?: "Não foi possível completar a ação. Tente novamente."
            )
        }
    }

    fun warnIfTruncated(resource: String, totalElements: Long, returned: Int) {
        if (totalElements > 100) {
            Logger.getLogger("InnovationApi").warning(
                "$resource totalElements=$totalElements returned=$returned; página limitada a 100"
            )
        }
    }
}
