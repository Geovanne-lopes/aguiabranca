package br.com.fiap.aguiabranca.auth.api.dto;

import jakarta.validation.constraints.NotBlank;

public record RefreshRequest(
        @NotBlank(message = "é obrigatório")
        String refreshToken
) {
}
