package br.com.fiap.challengeaguiabranca.data.repository

import br.com.fiap.challengeaguiabranca.data.remote.api.FakerApiService
import br.com.fiap.challengeaguiabranca.data.remote.mapper.UserMapper
import br.com.fiap.challengeaguiabranca.domain.model.User
import br.com.fiap.challengeaguiabranca.domain.repository.UserRepository

class UserRepositoryImpl(
    private val fakerApiService: FakerApiService
) : UserRepository {

    override suspend fun fetchSeedUsers(): List<User> {
        val response = fakerApiService.getUsers(quantity = 3)
        require(response.data.size >= 3) {
            "FakerAPI deve retornar pelo menos 3 usuários para mapear os perfis."
        }
        return UserMapper.fromFakerDtos(response.data.take(3))
    }
}
