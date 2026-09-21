package br.com.fiap.challengeaguiabranca.domain.error

/**
 * Falha de chamada remota já traduzida. A UI não vê Retrofit nem códigos HTTP crus.
 */
sealed class RemoteCallException(message: String) : Exception(message) {
    class Unauthorized(message: String = "Sessão expirada. Entre novamente.") : RemoteCallException(message)
    class Forbidden : RemoteCallException("Sem permissão para esta ação.")
    class NotFound(message: String = "Registro não encontrado.") : RemoteCallException(message)
    class Conflict(message: String, val code: String? = null) : RemoteCallException(message)
    class Validation(message: String) : RemoteCallException(message)
    class Server(message: String = "Não foi possível completar a ação. Tente novamente.") : RemoteCallException(message)
    class Network(message: String = "Não foi possível conectar. Verifique sua internet.") : RemoteCallException(message)
}
