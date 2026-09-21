package br.com.fiap.aguiabranca.auth.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ResetPasswordRequest(
        @NotBlank(message = "é obrigatório")
        @Email(message = "deve ser um e-mail válido")
        String email,
        @NotBlank(message = "é obrigatório")
        @Size(min = 4, max = 72, message = "deve ter entre 4 e 72 caracteres")
        String newPassword
) {
}
