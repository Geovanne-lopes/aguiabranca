package br.com.fiap.aguiabranca.ai.api;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.fiap.aguiabranca.ai.api.dto.AiInsightResponse;
import br.com.fiap.aguiabranca.ai.api.dto.GenerateInsightRequest;
import br.com.fiap.aguiabranca.ai.application.AiInsightService;
import br.com.fiap.aguiabranca.security.current.CurrentUserAccessor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/ai/insights")
@Tag(name = "AI")
@SecurityRequirement(name = "bearer-jwt")
public class AiInsightController {

    private final AiInsightService aiInsightService;
    private final CurrentUserAccessor currentUserAccessor;

    public AiInsightController(AiInsightService aiInsightService, CurrentUserAccessor currentUserAccessor) {
        this.aiInsightService = aiInsightService;
        this.currentUserAccessor = currentUserAccessor;
    }

    @PostMapping
    @PreAuthorize("hasRole('LEADER')")
    @Operation(summary = "Gera um insight interpretativo com Gemini. Falha, timeout ou key ausente devolvem 200 source=FALLBACK. MANAGER e OPERATOR recebem 403.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "source GEMINI ou FALLBACK, com disclaimer"),
            @ApiResponse(responseCode = "400", description = "from posterior a to", content = @Content),
            @ApiResponse(responseCode = "403", description = "MANAGER ou OPERATOR", content = @Content),
            @ApiResponse(responseCode = "429", description = "Limite de 5 gerações a cada 10 minutos", content = @Content)
    })
    public AiInsightResponse generate(@RequestBody(required = false) GenerateInsightRequest request) {
        return aiInsightService.generate(currentUserAccessor.require(), request);
    }

    @GetMapping("/latest")
    @PreAuthorize("hasRole('LEADER')")
    @Operation(summary = "Último insight SUCCESS ou FALLBACK do líder autenticado. 404 se ele nunca gerou.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Último insight do líder"),
            @ApiResponse(responseCode = "403", description = "MANAGER ou OPERATOR", content = @Content),
            @ApiResponse(responseCode = "404", description = "Nunca gerou", content = @Content)
    })
    public AiInsightResponse latest() {
        return aiInsightService.latest(currentUserAccessor.require());
    }
}
