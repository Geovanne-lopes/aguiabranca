package br.com.fiap.aguiabranca.dashboard.api.dto;

public record RankingItemResponse(
        String authorId,
        String name,
        String email,
        int ideasSubmitted,
        int ideasApproved
) {
}
