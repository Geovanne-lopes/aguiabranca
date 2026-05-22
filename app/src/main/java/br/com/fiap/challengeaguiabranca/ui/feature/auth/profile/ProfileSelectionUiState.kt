package br.com.fiap.challengeaguiabranca.ui.feature.auth.profile

import br.com.fiap.challengeaguiabranca.domain.model.User
import br.com.fiap.challengeaguiabranca.domain.model.UserRole

data class ProfileSelectionUiState(
    val isLoading: Boolean = true,
    val users: List<User> = emptyList(),
    val errorMessage: String? = null,
    val isSaving: Boolean = false
) {
    fun userForRole(role: UserRole): User? = users.find { it.role == role }
}
