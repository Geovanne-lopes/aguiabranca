package br.com.fiap.aguiabranca.user.api.dto;

import br.com.fiap.aguiabranca.user.domain.UserRole;
import br.com.fiap.aguiabranca.user.infra.UserDocument;

public record UserResponse(
        String id,
        String name,
        String email,
        String avatarUrl,
        UserRole role
) {
    public static UserResponse from(UserDocument user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getAvatarUrl(),
                user.getRole()
        );
    }
}
