package br.com.fiap.aguiabranca.dashboard.api.dto;

import java.util.List;

public record StrategiesResponse(
        List<StrategyItemResponse> items
) {
}
