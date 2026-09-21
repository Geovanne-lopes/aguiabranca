package br.com.fiap.aguiabranca.dashboard.domain;

import java.time.Instant;

import br.com.fiap.aguiabranca.project.domain.ProjectStatus;

/**
 * Números de um projeto usados só no cálculo. Sem título, descrição ou nomes.
 */
public record ProjectFigures(
        double investmentAmount,
        double obtainedProfit,
        double productivityGainPercent,
        ProjectStatus status,
        Instant createdAt,
        Instant deadline,
        String guidelineId
) {
}
