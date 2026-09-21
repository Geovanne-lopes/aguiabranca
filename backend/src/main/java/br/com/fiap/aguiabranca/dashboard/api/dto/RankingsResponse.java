package br.com.fiap.aguiabranca.dashboard.api.dto;

import java.util.List;

public record RankingsResponse(
        List<RankingItemResponse> items
) {
}
