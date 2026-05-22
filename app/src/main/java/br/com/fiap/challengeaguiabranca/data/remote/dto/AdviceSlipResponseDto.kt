package br.com.fiap.challengeaguiabranca.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class AdviceSlipResponseDto(
    val slip: AdviceSlipDto
)

@Serializable
data class AdviceSlipDto(
    val id: Int,
    val advice: String
)
