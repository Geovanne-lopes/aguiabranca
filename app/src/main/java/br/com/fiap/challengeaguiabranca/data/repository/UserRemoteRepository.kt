package br.com.fiap.challengeaguiabranca.data.repository

import br.com.fiap.challengeaguiabranca.data.remote.ApiCaller
import br.com.fiap.challengeaguiabranca.data.remote.api.InnovationApi
import br.com.fiap.challengeaguiabranca.data.remote.dto.UpdateProfileRequestDto
import br.com.fiap.challengeaguiabranca.data.remote.mapper.toDomain
import br.com.fiap.challengeaguiabranca.domain.model.User
import br.com.fiap.challengeaguiabranca.domain.model.UserRole
import br.com.fiap.challengeaguiabranca.domain.repository.UserRepository

class UserRemoteRepository(
    private val api: InnovationApi,
    private val apiCaller: ApiCaller
) : UserRepository {

    override suspend fun fetchSeedUsers(): List<User> = emptyList()

    override suspend fun listByRole(role: UserRole): List<User> {
        val page = apiCaller.execute { api.listUsers(role = role.name) }
        apiCaller.warnIfTruncated("users", page.totalElements, page.content.size)
        return page.content.map { it.toDomain() }
    }

    override suspend fun updateProfile(name: String, email: String, avatarUrl: String?): User =
        apiCaller.execute {
            api.updateMe(
                UpdateProfileRequestDto(
                    name = name,
                    email = email,
                    avatarUrl = avatarUrl
                )
            )
        }.toDomain()
}
