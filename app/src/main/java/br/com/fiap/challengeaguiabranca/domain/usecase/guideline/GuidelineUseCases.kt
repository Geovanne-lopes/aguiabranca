package br.com.fiap.challengeaguiabranca.domain.usecase.guideline

import br.com.fiap.challengeaguiabranca.domain.model.StrategicGuideline
import br.com.fiap.challengeaguiabranca.domain.repository.GuidelineRepository
import br.com.fiap.challengeaguiabranca.domain.util.IdGenerator
import kotlinx.coroutines.flow.Flow

class ObserveGuidelinesUseCase(
    private val guidelineRepository: GuidelineRepository
) {
    operator fun invoke(): Flow<List<StrategicGuideline>> =
        guidelineRepository.observeAll()
}

class CreateGuidelineUseCase(
    private val guidelineRepository: GuidelineRepository
) {
    suspend operator fun invoke(
        title: String,
        content: String,
        authorId: String
    ): StrategicGuideline {
        val now = System.currentTimeMillis()
        val guideline = StrategicGuideline(
            id = IdGenerator.newId(),
            title = title.trim(),
            content = content.trim(),
            authorId = authorId,
            createdAtEpochMillis = now,
            updatedAtEpochMillis = now
        )
        guidelineRepository.insert(guideline)
        return guideline
    }
}

class UpdateGuidelineUseCase(
    private val guidelineRepository: GuidelineRepository
) {
    suspend operator fun invoke(guideline: StrategicGuideline) {
        guidelineRepository.update(
            guideline.copy(updatedAtEpochMillis = System.currentTimeMillis())
        )
    }
}

class DeleteGuidelineUseCase(
    private val guidelineRepository: GuidelineRepository
) {
    suspend operator fun invoke(guidelineId: String) {
        guidelineRepository.delete(guidelineId)
    }
}
