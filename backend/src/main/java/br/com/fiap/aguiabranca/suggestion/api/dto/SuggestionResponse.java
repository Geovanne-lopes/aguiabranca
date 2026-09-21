package br.com.fiap.aguiabranca.suggestion.api.dto;

import java.time.Instant;

import br.com.fiap.aguiabranca.suggestion.infra.SuggestionDocument;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Sugestão persistida. authorName corresponde ao managerName legado do Android.")
public record SuggestionResponse(
        String id,
        String authorUserId,
        String authorName,
        String targetUserId,
        String targetEmail,
        String targetName,
        String message,
        Instant createdAt
) {
    public static SuggestionResponse from(SuggestionDocument document) {
        return new SuggestionResponse(
                document.getId(),
                document.getAuthorUserId(),
                document.getAuthorName(),
                document.getTargetUserId(),
                document.getTargetEmail(),
                document.getTargetName(),
                document.getMessage(),
                document.getCreatedAt()
        );
    }
}
