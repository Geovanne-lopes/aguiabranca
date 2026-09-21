package br.com.fiap.aguiabranca.user.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UpdateProfileRequest(
        @Size(min = 2, max = 80, message = "deve ter entre 2 e 80 caracteres")
        String name,
        @Email(message = "deve ser um e-mail válido")
        String email,
        @Size(max = 2048, message = "deve ter no máximo 2048 caracteres")
        String avatarUrl
) {
    public boolean hasAnyField() {
        return isPresent(name) || isPresent(email) || avatarUrl != null;
    }

    private boolean isPresent(String value) {
        return value != null && !value.isBlank();
    }
}
