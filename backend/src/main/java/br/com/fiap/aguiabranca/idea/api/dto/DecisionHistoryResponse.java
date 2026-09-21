package br.com.fiap.aguiabranca.idea.api.dto;

import java.time.Instant;

import br.com.fiap.aguiabranca.idea.domain.IdeaStatus;
import br.com.fiap.aguiabranca.idea.infra.DecisionHistoryItem;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Item append-only do histórico de decisão")
public record DecisionHistoryResponse(
        IdeaStatus status,
        String actorUserId,
        String actorName,
        String justification,
        Instant occurredAt
) {
    public static DecisionHistoryResponse from(DecisionHistoryItem item, String actorName) {
        return new DecisionHistoryResponse(
                item.getStatus(),
                item.getActorUserId(),
                actorName,
                item.getJustification(),
                item.getOccurredAt()
        );
    }
}
