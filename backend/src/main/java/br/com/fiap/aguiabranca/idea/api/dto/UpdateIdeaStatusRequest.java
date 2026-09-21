package br.com.fiap.aguiabranca.idea.api.dto;

import br.com.fiap.aguiabranca.idea.domain.IdeaStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "Decisão de status. PENDING não é aceito. REJECTED exige justification de 10 a 1000 caracteres.")
public record UpdateIdeaStatusRequest(
        @NotNull(message = "é obrigatório")
        @Schema(example = "REJECTED", description = "APPROVED, REJECTED ou PRIORITIZED")
        IdeaStatus status,
        @Size(max = 1000, message = "deve ter no máximo 1000 caracteres")
        @Schema(example = "Fora da diretriz de sustentabilidade deste semestre.", nullable = true)
        String justification
) {
}
