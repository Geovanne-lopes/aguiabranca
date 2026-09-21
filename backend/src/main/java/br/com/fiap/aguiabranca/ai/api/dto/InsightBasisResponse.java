package br.com.fiap.aguiabranca.ai.api.dto;

public record InsightBasisResponse(
        int totalProjects,
        double overallRoiPercent,
        double totalInvestment
) {
}
