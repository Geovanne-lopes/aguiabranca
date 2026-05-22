package br.com.fiap.challengeaguiabranca.data.remote.api

import br.com.fiap.challengeaguiabranca.data.remote.dto.FakerUsersResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface FakerApiService {

    @GET("api/v1/users")
    suspend fun getUsers(
        @Query("_quantity") quantity: Int = 3
    ): FakerUsersResponseDto
}
