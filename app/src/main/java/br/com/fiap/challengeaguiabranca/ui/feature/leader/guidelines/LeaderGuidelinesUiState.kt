package br.com.fiap.challengeaguiabranca.ui.feature.leader.guidelines

import br.com.fiap.challengeaguiabranca.domain.model.StrategicGuideline

data class LeaderGuidelinesUiState(
    val guidelines: List<StrategicGuideline> = emptyList(),
    val isLoading: Boolean = true,
    val form: GuidelineFormState = GuidelineFormState(),
    val isSaving: Boolean = false,
    val message: String? = null
)

data class GuidelineFormState(
    val isVisible: Boolean = false,
    val editingId: String? = null,
    val title: String = "",
    val content: String = ""
)
