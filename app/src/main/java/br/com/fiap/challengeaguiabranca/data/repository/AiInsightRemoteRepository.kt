package br.com.fiap.challengeaguiabranca.data.repository

import br.com.fiap.challengeaguiabranca.data.remote.ApiCaller
import br.com.fiap.challengeaguiabranca.data.remote.api.InnovationApi
import br.com.fiap.challengeaguiabranca.data.remote.mapper.toDomain
import br.com.fiap.challengeaguiabranca.domain.error.RemoteCallException
import br.com.fiap.challengeaguiabranca.domain.model.AiInsight
import br.com.fiap.challengeaguiabranca.domain.repository.AiInsightRepository

class AiInsightRemoteRepository(
    private val api: InnovationApi,
    private val apiCaller: ApiCaller
) : AiInsightRepository {

    override suspend fun latestOrNull(): AiInsight? =
        try {
            apiCaller.execute { api.latestAiInsight() }.toDomain()
        } catch (error: RemoteCallException.NotFound) {
            null
        }

    override suspend fun generate(): AiInsight =
        apiCaller.execute { api.generateAiInsight() }.toDomain()
}
