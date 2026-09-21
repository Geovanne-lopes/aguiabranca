package br.com.fiap.aguiabranca.insight.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.fiap.aguiabranca.insight.api.dto.DailyInsightResponse;
import br.com.fiap.aguiabranca.insight.application.DailyInsightService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/insights")
@Tag(name = "Insights")
@SecurityRequirement(name = "bearer-jwt")
public class InsightController {

    private final DailyInsightService dailyInsightService;

    public InsightController(DailyInsightService dailyInsightService) {
        this.dailyInsightService = dailyInsightService;
    }

    @GetMapping("/daily")
    @Operation(summary = "Insight do dia via AdviceSlip (somente backend). Timeout 5s, 1 retry, cache de 60 min. Fornecedor fora do ar responde 200 com fallback.")
    public DailyInsightResponse daily() {
        return dailyInsightService.daily();
    }
}
