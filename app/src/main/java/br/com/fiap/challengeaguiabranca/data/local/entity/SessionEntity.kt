package br.com.fiap.challengeaguiabranca.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import br.com.fiap.challengeaguiabranca.domain.model.UserRole

/**
 * Sessão única (linha fixa id = 1) do usuário logado.
 */
@Entity(tableName = "user_session")
data class SessionEntity(
    @PrimaryKey val id: Int = SESSION_ROW_ID,
    val userId: String,
    val name: String,
    val email: String,
    val avatarUrl: String?,
    val role: UserRole
) {
    companion object {
        const val SESSION_ROW_ID = 1
    }
}
