package br.com.fiap.challengeaguiabranca.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequestDto(
    val email: String,
    val password: String
)

@Serializable
data class RegisterRequestDto(
    val name: String,
    val email: String,
    val password: String,
    val role: String
)

@Serializable
data class RefreshRequestDto(
    val refreshToken: String
)

@Serializable
data class LogoutRequestDto(
    val refreshToken: String? = null
)

@Serializable
data class ResetPasswordRequestDto(
    val email: String,
    val newPassword: String
)

@Serializable
data class AuthResponseDto(
    val accessToken: String,
    val refreshToken: String,
    val tokenType: String = "Bearer",
    val expiresIn: Long = 0,
    val user: UserResponseDto
)

@Serializable
data class UserResponseDto(
    val id: String,
    val name: String,
    val email: String,
    val avatarUrl: String? = null,
    val role: String
)

@Serializable
data class UpdateProfileRequestDto(
    val name: String? = null,
    val email: String? = null,
    val avatarUrl: String? = null
)
