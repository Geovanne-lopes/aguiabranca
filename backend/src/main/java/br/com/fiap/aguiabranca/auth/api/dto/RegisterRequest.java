package br.com.fiap.aguiabranca.auth.api.dto;

import br.com.fiap.aguiabranca.user.domain.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "é obrigatório")
        @Size(min = 2, max = 80, message = "deve ter entre 2 e 80 caracteres")
        String name,
        @NotBlank(message = "é obrigatório")
        @Email(message = "deve ser um e-mail válido")
        String email,
        @NotBlank(message = "é obrigatório")
        @Size(min = 4, max = 72, message = "deve ter entre 4 e 72 caracteres")
        String password,
        @NotNull(message = "é obrigatório")
        UserRole role
) {
}
