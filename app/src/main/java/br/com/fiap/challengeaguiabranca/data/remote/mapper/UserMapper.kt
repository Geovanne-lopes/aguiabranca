package br.com.fiap.challengeaguiabranca.data.remote.mapper

import br.com.fiap.challengeaguiabranca.data.remote.dto.FakerUserDto
import br.com.fiap.challengeaguiabranca.domain.model.User
import br.com.fiap.challengeaguiabranca.domain.model.UserRole

object UserMapper {

    private val roleByIndex = listOf(
        UserRole.OPERATOR,
        UserRole.MANAGER,
        UserRole.LEADER
    )

    fun fromFakerDtos(dtos: List<FakerUserDto>): List<User> =
        dtos.mapIndexed { index, dto ->
            User(
                id = dto.uuid,
                name = "${dto.firstname} ${dto.lastname}".trim(),
                email = dto.email,
                avatarUrl = dto.image,
                role = roleByIndex.getOrElse(index) { UserRole.OPERATOR }
            )
        }
}
