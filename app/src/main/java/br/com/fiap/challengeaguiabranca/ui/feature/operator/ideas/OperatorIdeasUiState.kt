package br.com.fiap.challengeaguiabranca.ui.feature.operator.ideas

import br.com.fiap.challengeaguiabranca.domain.model.Idea
import br.com.fiap.challengeaguiabranca.domain.model.IdeaCategory
import br.com.fiap.challengeaguiabranca.domain.model.StrategicGuideline

data class IdeaFormState(
    val title: String = "",
    val description: String = "",
    val category: IdeaCategory = IdeaCategory.PROCESS,
    val guidelineId: String? = null,
    val editingId: String? = null,
    val isVisible: Boolean = false
)

data class OperatorIdeasUiState(
    val ideas: List<Idea> = emptyList(),
    val guidelines: List<StrategicGuideline> = emptyList(),
    val form: IdeaFormState = IdeaFormState(),
    val pendingDeleteId: String? = null,
    val isLoading: Boolean = true,
    val isSubmitting: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)
