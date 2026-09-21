package br.com.fiap.challengeaguiabranca.domain.usecase.manager

import br.com.fiap.challengeaguiabranca.domain.model.ManagerSuggestion
import br.com.fiap.challengeaguiabranca.domain.model.OperatorActivity
import br.com.fiap.challengeaguiabranca.domain.repository.DashboardRepository
import br.com.fiap.challengeaguiabranca.domain.repository.ManagerSuggestionRepository
import br.com.fiap.challengeaguiabranca.domain.util.IdGenerator
import kotlinx.coroutines.flow.Flow

class GetOperatorActivityRankingUseCase(
    private val dashboardRepository: DashboardRepository
) {
    operator fun invoke(): Flow<List<OperatorActivity>> =
        dashboardRepository.observeRankings(period = "ALL")
}

class GetMonthlyOperatorRankingUseCase(
    private val dashboardRepository: DashboardRepository
) {
    operator fun invoke(): Flow<List<OperatorActivity>> =
        dashboardRepository.observeRankings(period = "MONTH")
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
