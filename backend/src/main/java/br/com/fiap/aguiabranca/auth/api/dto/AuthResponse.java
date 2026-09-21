package br.com.fiap.aguiabranca.auth.api.dto;

import br.com.fiap.aguiabranca.user.api.dto.UserResponse;

public record AuthResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        long expiresIn,
        UserResponse user
) {
}
