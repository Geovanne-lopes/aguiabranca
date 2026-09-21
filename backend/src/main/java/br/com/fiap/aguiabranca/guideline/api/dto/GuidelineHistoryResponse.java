package br.com.fiap.aguiabranca.guideline.api.dto;

import java.time.Instant;

import br.com.fiap.aguiabranca.guideline.domain.GuidelineHistoryAction;
import br.com.fiap.aguiabranca.guideline.infra.GuidelineHistoryDocument;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Evento append-only do histórico de uma diretriz")
public record GuidelineHistoryResponse(
        String id,
        String guidelineId,
        int version,
        GuidelineHistoryAction action,
        String title,
        String content,
        String category,
        String campaign,
        String actorUserId,
        String actorName,
        Instant occurredAt
) {
    public static GuidelineHistoryResponse from(GuidelineHistoryDocument history, String actorName) {
        return new GuidelineHistoryResponse(
                history.getId(),
                history.getGuidelineId(),
                history.getVersion(),
                history.getAction(),
                history.getTitle(),
                history.getContent(),
                history.getCategory(),
                history.getCampaign(),
                history.getActorUserId(),
                actorName,
                history.getOccurredAt()
        );
    }
}
