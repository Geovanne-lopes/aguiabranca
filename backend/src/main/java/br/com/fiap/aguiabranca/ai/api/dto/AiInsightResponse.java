package br.com.fiap.aguiabranca.ai.api.dto;

import java.time.Instant;

public record AiInsightResponse(
        String id,
        String content,
        String source,
        String disclaimer,
        Instant createdAt,
        InsightBasisResponse basedOn
) {
}
