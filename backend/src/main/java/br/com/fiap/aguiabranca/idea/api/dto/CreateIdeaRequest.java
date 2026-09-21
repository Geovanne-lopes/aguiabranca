package br.com.fiap.aguiabranca.idea.api.dto;

import br.com.fiap.aguiabranca.idea.domain.IdeaCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "Criação de ideia. authorId do body é ignorado: o autor é o usuário autenticado.")
public record CreateIdeaRequest(
        @NotBlank(message = "é obrigatório")
        @Size(min = 3, max = 120, message = "deve ter entre 3 e 120 caracteres")
        @Schema(example = "Coleta seletiva nas lojas")
        String title,
        @NotBlank(message = "é obrigatório")
        @Size(min = 10, max = 4000, message = "deve ter entre 10 e 4000 caracteres")
        @Schema(example = "Instalar pontos de coleta com meta mensal de volume.")
        String description,
        @NotNull(message = "é obrigatório")
        @Schema(example = "SUSTAINABILITY")
        IdeaCategory category,
        @Schema(description = "UUID de diretriz existente. Omitir ou null usa a mais recentemente atualizada.", nullable = true)
        String guidelineId
) {
    public CreateIdeaRequest {
        title = title == null ? null : title.trim();
        description = description == null ? null : description.trim();
        if (guidelineId != null) {
            guidelineId = guidelineId.trim();
            if (guidelineId.isEmpty()) {
                guidelineId = null;
            }
        }
    }
}
