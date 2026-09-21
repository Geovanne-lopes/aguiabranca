package br.com.fiap.challengeaguiabranca.domain.usecase.ai

import br.com.fiap.challengeaguiabranca.domain.model.AiInsight
import br.com.fiap.challengeaguiabranca.domain.repository.AiInsightRepository

class GetLatestAiInsightUseCase(
    private val aiInsightRepository: AiInsightRepository
) {
    suspend operator fun invoke(): AiInsight? = aiInsightRepository.latestOrNull()
}

class GenerateAiInsightUseCase(
    private val aiInsightRepository: AiInsightRepository
) {
    suspend operator fun invoke(): AiInsight = aiInsightRepository.generate()
}
