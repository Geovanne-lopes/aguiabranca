package br.com.fiap.challengeaguiabranca.data.remote.mapper

import br.com.fiap.challengeaguiabranca.data.remote.dto.UserResponseDto
import br.com.fiap.challengeaguiabranca.domain.model.User
import br.com.fiap.challengeaguiabranca.domain.model.UserRole

fun UserResponseDto.toDomain(): User = User(
    id = id,
    name = name,
    email = email,
    avatarUrl = avatarUrl,
    role = role.toUserRole()
)

internal fun String.toUserRole(): UserRole =
    enumValues<UserRole>().firstOrNull { it.name == this } ?: UserRole.OPERATOR
