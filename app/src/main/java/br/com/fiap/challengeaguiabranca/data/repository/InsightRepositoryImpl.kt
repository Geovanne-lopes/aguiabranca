package br.com.fiap.challengeaguiabranca.data.repository

import br.com.fiap.challengeaguiabranca.data.remote.api.AdviceApiService
import br.com.fiap.challengeaguiabranca.data.remote.mapper.InsightMapper
import br.com.fiap.challengeaguiabranca.domain.model.DailyInsight
import br.com.fiap.challengeaguiabranca.domain.repository.InsightRepository

class InsightRepositoryImpl(
    private val adviceApiService: AdviceApiService
) : InsightRepository {

    override suspend fun fetchDailyInsight(): DailyInsight {
        val response = adviceApiService.getRandomAdvice()
        return InsightMapper.fromAdviceSlip(response.slip)
    }
}
