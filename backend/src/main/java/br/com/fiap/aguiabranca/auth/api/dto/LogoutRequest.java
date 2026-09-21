package br.com.fiap.aguiabranca.auth.api.dto;

public record LogoutRequest(
        String refreshToken
) {
}
