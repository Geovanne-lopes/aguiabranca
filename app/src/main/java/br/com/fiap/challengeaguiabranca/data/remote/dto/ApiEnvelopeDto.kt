package br.com.fiap.challengeaguiabranca.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ApiErrorDetailDto(
    val field: String? = null,
    val issue: String? = null
)

@Serializable
data class ApiErrorDto(
    val status: Int = 0,
    val code: String? = null,
    val message: String? = null,
    val path: String? = null,
    val traceId: String? = null,
    val details: List<ApiErrorDetailDto> = emptyList()
)

@Serializable
data class ApiPageDto<T>(
    val content: List<T> = emptyList(),
    val page: Int = 0,
    val size: Int = 0,
    val totalElements: Long = 0,
    val totalPages: Int = 0
)
