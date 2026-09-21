package br.com.fiap.aguiabranca.idea.api.dto;

import java.time.Instant;
import java.util.List;

import br.com.fiap.aguiabranca.idea.domain.IdeaCategory;
import br.com.fiap.aguiabranca.idea.domain.IdeaStatus;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Ideia de inovação")
public record IdeaResponse(
        String id,
        String title,
        String description,
        IdeaCategory category,
        String authorId,
        String authorName,
        IdeaStatus status,
        String guidelineId,
        String guidelineTitle,
        List<DecisionHistoryResponse> decisionHistory,
        Instant createdAt,
        Instant updatedAt
) {
}
