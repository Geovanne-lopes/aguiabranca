package br.com.fiap.challengeaguiabranca.ui.navigation

import br.com.fiap.challengeaguiabranca.domain.model.UserRole

object Routes {
    const val LOGIN = "login"
    const val PROFILE_SELECTION = "profile_selection"
    const val OPERATOR_HOME = "operator_home"
    const val MANAGER_HOME = "manager_home"
    const val LEADER_HOME = "leader_home"

    fun homeForRole(role: UserRole): String = when (role) {
        UserRole.OPERATOR -> OPERATOR_HOME
        UserRole.MANAGER -> MANAGER_HOME
        UserRole.LEADER -> LEADER_HOME
    }
}
