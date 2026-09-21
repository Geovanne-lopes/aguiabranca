package br.com.fiap.challengeaguiabranca.data.repository

import br.com.fiap.challengeaguiabranca.data.remote.ApiCaller
import br.com.fiap.challengeaguiabranca.data.remote.api.InnovationApi
import br.com.fiap.challengeaguiabranca.data.remote.mapper.toDomain
import br.com.fiap.challengeaguiabranca.domain.model.DailyInsight
import br.com.fiap.challengeaguiabranca.domain.repository.InsightRepository

class InsightRemoteRepository(
    private val api: InnovationApi,
    private val apiCaller: ApiCaller
) : InsightRepository {

    override suspend fun fetchDailyInsight(): DailyInsight =
        apiCaller.execute { api.dailyInsight() }.toDomain()
}
