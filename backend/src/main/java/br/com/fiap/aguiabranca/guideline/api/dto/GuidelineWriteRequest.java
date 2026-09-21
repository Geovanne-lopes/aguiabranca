package br.com.fiap.aguiabranca.guideline.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Campos editáveis de uma diretriz estratégica")
public record GuidelineWriteRequest(
        @NotBlank(message = "é obrigatório")
        @Size(min = 3, max = 120, message = "deve ter entre 3 e 120 caracteres")
        @Schema(example = "Sustentabilidade Corporativa")
        String title,
        @NotBlank(message = "é obrigatório")
        @Size(min = 10, max = 4000, message = "deve ter entre 10 e 4000 caracteres")
        @Schema(example = "Incentivar ideias com impacto ambiental mensurável.")
        String content,
        @Size(max = 40, message = "deve ter no máximo 40 caracteres")
        @Schema(example = "ESG")
        String category,
        @Size(max = 80, message = "deve ter no máximo 80 caracteres")
        @Schema(example = "NetZero")
        String campaign
) {
}
