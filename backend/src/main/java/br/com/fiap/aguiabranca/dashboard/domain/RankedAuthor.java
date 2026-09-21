package br.com.fiap.aguiabranca.dashboard.domain;

public record RankedAuthor(
        String authorId,
        String name,
        String email,
        int ideasSubmitted,
        int ideasApproved
) {
}
