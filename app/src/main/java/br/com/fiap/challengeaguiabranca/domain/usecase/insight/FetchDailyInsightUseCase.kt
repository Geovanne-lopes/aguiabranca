package br.com.fiap.challengeaguiabranca.domain.usecase.insight

import br.com.fiap.challengeaguiabranca.domain.model.DailyInsight
import br.com.fiap.challengeaguiabranca.domain.repository.InsightRepository

class FetchDailyInsightUseCase(
    private val insightRepository: InsightRepository
) {
    suspend operator fun invoke(): DailyInsight =
        insightRepository.fetchDailyInsight()
}
