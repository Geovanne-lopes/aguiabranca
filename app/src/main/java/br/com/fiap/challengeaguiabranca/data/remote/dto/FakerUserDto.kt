package br.com.fiap.challengeaguiabranca.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FakerUsersResponseDto(
    val status: String,
    val code: Int,
    val total: Int,
    val data: List<FakerUserDto>
)

@Serializable
data class FakerUserDto(
    val id: Int,
    val uuid: String,
    val firstname: String,
    val lastname: String,
    val username: String,
    val email: String,
    val image: String
)
