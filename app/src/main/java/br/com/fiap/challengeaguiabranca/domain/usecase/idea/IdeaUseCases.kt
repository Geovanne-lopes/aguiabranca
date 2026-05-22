package br.com.fiap.challengeaguiabranca.domain.usecase.idea

import br.com.fiap.challengeaguiabranca.domain.model.Idea
import br.com.fiap.challengeaguiabranca.domain.model.IdeaCategory
import br.com.fiap.challengeaguiabranca.domain.model.IdeaStatus
import br.com.fiap.challengeaguiabranca.domain.repository.IdeaRepository
import br.com.fiap.challengeaguiabranca.domain.util.IdGenerator
import kotlinx.coroutines.flow.Flow

class SubmitIdeaUseCase(
    private val ideaRepository: IdeaRepository
) {
    suspend operator fun invoke(
        title: String,
        description: String,
        category: IdeaCategory,
        authorId: String
    ): Idea {
        val idea = Idea(
            id = IdGenerator.newId(),
            title = title.trim(),
            description = description.trim(),
            category = category,
            authorId = authorId
        )
        ideaRepository.insert(idea)
        return idea
    }
}

class ObserveIdeasByAuthorUseCase(
    private val ideaRepository: IdeaRepository
) {
    operator fun invoke(authorId: String): Flow<List<Idea>> =
        ideaRepository.observeByAuthor(authorId)
}

class ObserveAllIdeasUseCase(
    private val ideaRepository: IdeaRepository
) {
    operator fun invoke(): Flow<List<Idea>> = ideaRepository.observeAll()
}

class UpdateIdeaStatusUseCase(
    private val ideaRepository: IdeaRepository
) {
    suspend operator fun invoke(ideaId: String, status: IdeaStatus) {
        ideaRepository.updateStatus(ideaId, status)
    }
}

class GetPendingIdeasCountUseCase(
    private val ideaRepository: IdeaRepository
) {
    suspend operator fun invoke(): Int =
        ideaRepository.countByStatus(IdeaStatus.PENDING)
}
