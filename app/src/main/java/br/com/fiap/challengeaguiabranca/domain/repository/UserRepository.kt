package br.com.fiap.challengeaguiabranca.domain.repository

import br.com.fiap.challengeaguiabranca.domain.model.User

interface UserRepository {
    suspend fun fetchSeedUsers(): List<User>
}
