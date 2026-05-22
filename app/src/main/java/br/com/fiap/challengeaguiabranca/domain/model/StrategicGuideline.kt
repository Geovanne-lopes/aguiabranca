package br.com.fiap.challengeaguiabranca.domain.model

/**
 * Orientação estratégica criada pelo Líder; visível em leitura para Operador e Gestor.
 */
data class StrategicGuideline(
    val id: String,
    val title: String,
    val content: String,
    val authorId: String,
    val createdAtEpochMillis: Long = System.currentTimeMillis(),
    val updatedAtEpochMillis: Long = System.currentTimeMillis()
)
