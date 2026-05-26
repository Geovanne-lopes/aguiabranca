package br.com.fiap.challengeaguiabranca.ui.feature.operator.home

import br.com.fiap.challengeaguiabranca.domain.model.IdeaCategory

enum class OperatorOverlay {
    NONE,
    ALL_IDEAS,
    SUBMIT_IDEA,
    COLLABORATORS
}

data class SubmitIdeaOverlayState(
    val category: IdeaCategory = IdeaCategory.PROCESS
)
