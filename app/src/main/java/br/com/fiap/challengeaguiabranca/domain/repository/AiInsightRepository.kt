package br.com.fiap.challengeaguiabranca.domain.repository

import br.com.fiap.challengeaguiabranca.domain.model.AiInsight

interface AiInsightRepository {
    suspend fun latestOrNull(): AiInsight?
    suspend fun generate(): AiInsight
}
