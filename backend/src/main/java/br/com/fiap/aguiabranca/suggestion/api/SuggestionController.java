package br.com.fiap.aguiabranca.suggestion.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.fiap.aguiabranca.common.api.ApiPage;
import br.com.fiap.aguiabranca.security.current.CurrentUserAccessor;
import br.com.fiap.aguiabranca.suggestion.api.dto.CreateSuggestionRequest;
import br.com.fiap.aguiabranca.suggestion.api.dto.SuggestionResponse;
import br.com.fiap.aguiabranca.suggestion.application.SuggestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/suggestions")
@Tag(name = "Suggestions")
@SecurityRequirement(name = "bearer-jwt")
public class SuggestionController {

    private final SuggestionService suggestionService;
    private final CurrentUserAccessor currentUserAccessor;

    public SuggestionController(SuggestionService suggestionService, CurrentUserAccessor currentUserAccessor) {
        this.suggestionService = suggestionService;
        this.currentUserAccessor = currentUserAccessor;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('MANAGER', 'LEADER')")
    @Operation(summary = "Enviar sugestão a um operador ativo (MANAGER e LEADER). OPERATOR recebe 403. Sem update nem delete.")
    public ResponseEntity<SuggestionResponse> create(@Valid @RequestBody CreateSuggestionRequest request) {
        SuggestionResponse created = suggestionService.create(currentUserAccessor.require(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    @Operation(summary = "Listar sugestões: OPERATOR vê as recebidas; MANAGER e LEADER veem as que enviaram. Ordenação padrão createdAt DESC.")
    public ApiPage<SuggestionResponse> list(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String direction
    ) {
        return suggestionService.list(currentUserAccessor.require(), page, size, sort, direction);
    }
}
