package br.com.fiap.aguiabranca.insight.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Insight do dia. id 0 e a mensagem padrão indicam fallback do AdviceSlip.")
public record DailyInsightResponse(
        int id,
        String message
) {
}
