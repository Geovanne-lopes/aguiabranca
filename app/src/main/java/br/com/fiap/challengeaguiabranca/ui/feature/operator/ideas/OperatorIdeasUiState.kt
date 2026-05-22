package br.com.fiap.challengeaguiabranca.ui.feature.operator.ideas

import br.com.fiap.challengeaguiabranca.domain.model.Idea
import br.com.fiap.challengeaguiabranca.domain.model.IdeaCategory

data class IdeaFormState(
    val title: String = "",
    val description: String = "",
    val category: IdeaCategory = IdeaCategory.PROCESS,
    val isVisible: Boolean = false
)

data class OperatorIdeasUiState(
    val ideas: List<Idea> = emptyList(),
    val form: IdeaFormState = IdeaFormState(),
    val isLoading: Boolean = true,
    val isSubmitting: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)
