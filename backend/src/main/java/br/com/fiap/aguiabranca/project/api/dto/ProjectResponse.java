package br.com.fiap.aguiabranca.project.api.dto;

import java.time.Instant;

import br.com.fiap.aguiabranca.project.domain.ProjectStatus;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Projeto. roiPercent é calculado na leitura e não é gravado.")
public record ProjectResponse(
        String id,
        String ideaId,
        String guidelineId,
        String guidelineTitle,
        String title,
        String description,
        ProjectStatus status,
        double investmentAmount,
        double obtainedProfit,
        double productivityGainPercent,
        @Schema(description = "((obtainedProfit - investmentAmount) / investmentAmount) * 100 quando investmentAmount > 0; senão 0.")
        double roiPercent,
        Instant deadline,
        String managerId,
        String managerName,
        Instant createdAt,
        Instant updatedAt
) {
}
