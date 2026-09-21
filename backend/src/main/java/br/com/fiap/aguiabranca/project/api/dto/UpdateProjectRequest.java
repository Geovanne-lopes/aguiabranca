package br.com.fiap.aguiabranca.project.api.dto;

import java.time.Instant;

import br.com.fiap.aguiabranca.project.domain.ProjectStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "Substitui os campos editáveis do projeto. ideaId, managerId e createdAt enviados no JSON são ignorados.")
public record UpdateProjectRequest(
        @NotBlank(message = "é obrigatório")
        @Size(min = 3, max = 120, message = "deve ter entre 3 e 120 caracteres")
        @Schema(example = "App mobile para fila express")
        String title,
        @NotBlank(message = "é obrigatório")
        @Size(min = 10, max = 4000, message = "deve ter entre 10 e 4000 caracteres")
        @Schema(example = "Escopo atualizado da fila express nas lojas.")
        String description,
        @NotNull(message = "é obrigatório")
        @Schema(example = "IN_DEVELOPMENT")
        ProjectStatus status,
        @NotNull(message = "é obrigatório")
        @DecimalMin(value = "0.0", message = "deve ser maior ou igual a zero")
        @Schema(example = "15000.0", minimum = "0")
        Double investmentAmount,
        @NotNull(message = "é obrigatório")
        @DecimalMin(value = "0.0", message = "deve ser maior ou igual a zero")
        @Schema(example = "22000.0", minimum = "0")
        Double obtainedProfit,
        @NotNull(message = "é obrigatório")
        @DecimalMin(value = "0.0", message = "deve estar entre 0 e 100")
        @DecimalMax(value = "100.0", message = "deve estar entre 0 e 100")
        @Schema(example = "12.5", minimum = "0", maximum = "100")
        Double productivityGainPercent,
        @Schema(example = "2026-12-31T00:00:00Z", nullable = true)
        Instant deadline,
        @Schema(description = "UUID de diretriz existente, ou null para desvincular.", nullable = true)
        String guidelineId
) {
    public UpdateProjectRequest {
        title = title == null ? null : title.trim();
        description = description == null ? null : description.trim();
        if (guidelineId != null) {
            guidelineId = guidelineId.trim();
            if (guidelineId.isEmpty()) {
                guidelineId = null;
            }
        }
    }
}
