package br.com.fiap.aguiabranca.project.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Cria o projeto a partir de uma ideia APPROVED ou PRIORITIZED. Título, descrição e diretriz são copiados da ideia.")
public record CreateProjectRequest(
        @NotBlank(message = "é obrigatório")
        @Schema(example = "idea-uuid")
        String ideaId
) {
    public CreateProjectRequest {
        ideaId = ideaId == null ? null : ideaId.trim();
    }
}
