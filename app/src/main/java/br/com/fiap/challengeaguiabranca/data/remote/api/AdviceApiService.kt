package br.com.fiap.challengeaguiabranca.data.remote.api

import br.com.fiap.challengeaguiabranca.data.remote.dto.AdviceSlipResponseDto
import retrofit2.http.GET

interface AdviceApiService {

    @GET("advice")
    suspend fun getRandomAdvice(): AdviceSlipResponseDto
}
