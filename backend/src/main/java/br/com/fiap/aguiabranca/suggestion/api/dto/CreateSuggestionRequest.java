package br.com.fiap.aguiabranca.suggestion.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Sugestão de gestor ou líder para um operador. O remetente é o usuário autenticado.")
public record CreateSuggestionRequest(
        @NotBlank(message = "é obrigatório")
        @Schema(example = "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa")
        String targetUserId,
        @NotBlank(message = "é obrigatório")
        @Size(min = 10, max = 1000, message = "deve ter entre 10 e 1000 caracteres")
        @Schema(example = "Priorize ideias alinhadas à diretriz de transformação digital.")
        String message
) {
    public CreateSuggestionRequest {
        targetUserId = targetUserId == null ? null : targetUserId.trim();
        message = message == null ? null : message.trim();
    }
}
