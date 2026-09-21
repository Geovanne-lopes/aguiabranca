package br.com.fiap.aguiabranca.guideline.api.dto;

import java.time.Instant;

import br.com.fiap.aguiabranca.guideline.infra.GuidelineDocument;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Diretriz estratégica vigente")
public record GuidelineResponse(
        String id,
        String title,
        String content,
        String category,
        String campaign,
        int version,
        String authorId,
        String authorName,
        Instant createdAt,
        Instant updatedAt
) {
    public static GuidelineResponse from(GuidelineDocument guideline, String authorName) {
        return new GuidelineResponse(
                guideline.getId(),
                guideline.getTitle(),
                guideline.getContent(),
                guideline.getCategory(),
                guideline.getCampaign(),
                guideline.getVersion(),
                guideline.getAuthorId(),
                authorName,
                guideline.getCreatedAt(),
                guideline.getUpdatedAt()
        );
    }
}
