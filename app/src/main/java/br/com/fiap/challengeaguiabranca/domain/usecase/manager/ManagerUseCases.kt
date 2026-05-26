package br.com.fiap.challengeaguiabranca.domain.usecase.manager

import br.com.fiap.challengeaguiabranca.domain.catalog.MockOperatorsCatalog
import br.com.fiap.challengeaguiabranca.domain.model.IdeaStatus
import br.com.fiap.challengeaguiabranca.domain.model.ManagerSuggestion
import br.com.fiap.challengeaguiabranca.domain.model.OperatorActivity
import br.com.fiap.challengeaguiabranca.domain.repository.IdeaRepository
import br.com.fiap.challengeaguiabranca.domain.repository.ManagerSuggestionRepository
import br.com.fiap.challengeaguiabranca.domain.util.IdGenerator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetOperatorActivityRankingUseCase(
    private val ideaRepository: IdeaRepository
) {
    operator fun invoke(): Flow<List<OperatorActivity>> =
        ideaRepository.observeAll().map { ideas ->
            ideas.groupBy { it.authorId }
                .map { (authorId, authorIdeas) ->
                    val mock = MockOperatorsCatalog.findById(authorId)
                    OperatorActivity(
                        authorId = authorId,
                        name = mock?.name ?: "Colaborador",
                        email = mock?.email ?: "",
                        ideasSubmitted = authorIdeas.size,
                        ideasApproved = authorIdeas.count {
                            it.status == IdeaStatus.APPROVED || it.status == IdeaStatus.PRIORITIZED
                        }
                    )
                }
                .sortedByDescending { it.ideasSubmitted }
        }
}

class GetMonthlyOperatorRankingUseCase(
    private val ideaRepository: IdeaRepository
) {
    operator fun invoke(): Flow<List<OperatorActivity>> =
        ideaRepository.observeAll().map { ideas ->
            val startOfMonth = startOfCurrentMonth()
            ideas
                .filter { it.createdAtEpochMillis >= startOfMonth }
                .groupBy { it.authorId }
                .map { (authorId, authorIdeas) ->
                    val mock = MockOperatorsCatalog.findById(authorId)
                    OperatorActivity(
                        authorId = authorId,
                        name = mock?.name ?: "Colaborador",
                        email = mock?.email ?: "",
                        ideasSubmitted = authorIdeas.size,
                        ideasApproved = authorIdeas.count {
                            it.status == IdeaStatus.APPROVED || it.status == IdeaStatus.PRIORITIZED
                        }
                    )
                }
                .sortedByDescending { it.ideasSubmitted }
        }

    private fun startOfCurrentMonth(): Long {
        val cal = java.util.Calendar.getInstance()
        cal.set(java.util.Calendar.DAY_OF_MONTH, 1)
        cal.set(java.util.Calendar.HOUR_OF_DAY, 0)
        cal.set(java.util.Calendar.MINUTE, 0)
        cal.set(java.util.Calendar.SECOND, 0)
        cal.set(java.util.Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }
}

class SendManagerSuggestionUseCase(
    private val suggestionRepository: ManagerSuggestionRepository
) {
    suspend operator fun invoke(
        managerName: String,
        targetAuthorId: String,
        targetEmail: String,
        targetName: String,
        message: String
    ): Result<ManagerSuggestion> {
        val trimmed = message.trim()
        when {
            trimmed.length < 10 ->
                return Result.failure(IllegalArgumentException("A sugestão deve ter pelo menos 10 caracteres."))
            targetAuthorId.isBlank() ->
                return Result.failure(IllegalArgumentException("Selecione um operador."))
        }
        val suggestion = ManagerSuggestion(
            id = IdGenerator.newId(),
            managerName = managerName,
            targetAuthorId = targetAuthorId,
            targetEmail = targetEmail,
            targetName = targetName,
            message = trimmed,
            createdAtEpochMillis = System.currentTimeMillis()
        )
        suggestionRepository.insert(suggestion)
        return Result.success(suggestion)
    }
}

class ObserveManagerSuggestionsUseCase(
    private val suggestionRepository: ManagerSuggestionRepository
) {
    operator fun invoke(): Flow<List<ManagerSuggestion>> = suggestionRepository.observeAll()
}

class ObserveSuggestionsForUserUseCase(
    private val suggestionRepository: ManagerSuggestionRepository
) {
    operator fun invoke(email: String, authorId: String): Flow<List<ManagerSuggestion>> =
        suggestionRepository.observeForTarget(email, authorId)
}
