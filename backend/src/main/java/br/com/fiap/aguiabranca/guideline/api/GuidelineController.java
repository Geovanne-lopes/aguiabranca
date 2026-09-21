package br.com.fiap.aguiabranca.guideline.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.fiap.aguiabranca.common.api.ApiPage;
import br.com.fiap.aguiabranca.guideline.api.dto.GuidelineHistoryResponse;
import br.com.fiap.aguiabranca.guideline.api.dto.GuidelineResponse;
import br.com.fiap.aguiabranca.guideline.api.dto.GuidelineWriteRequest;
import br.com.fiap.aguiabranca.guideline.application.GuidelineService;
import br.com.fiap.aguiabranca.security.current.CurrentUserAccessor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/guidelines")
@Tag(name = "Guidelines")
@SecurityRequirement(name = "bearer-jwt")
public class GuidelineController {

    private final GuidelineService guidelineService;
    private final CurrentUserAccessor currentUserAccessor;

    public GuidelineController(GuidelineService guidelineService, CurrentUserAccessor currentUserAccessor) {
        this.guidelineService = guidelineService;
        this.currentUserAccessor = currentUserAccessor;
    }

    @GetMapping
    @Operation(summary = "Listar estratégias vigentes")
    public ApiPage<GuidelineResponse> list(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String direction,
            @RequestParam(required = false) String q
    ) {
        return guidelineService.list(page, size, sort, direction, q);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obter uma estratégia vigente")
    public GuidelineResponse getById(@PathVariable String id) {
        return guidelineService.getById(id);
    }

    @PostMapping
    @PreAuthorize("hasRole('LEADER')")
    @Operation(summary = "Criar estratégia (somente LEADER)")
    public ResponseEntity<GuidelineResponse> create(@Valid @RequestBody GuidelineWriteRequest request) {
        GuidelineResponse created = guidelineService.create(currentUserAccessor.require(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('LEADER')")
    @Operation(summary = "Atualizar estratégia (somente LEADER)")
    public GuidelineResponse update(@PathVariable String id, @Valid @RequestBody GuidelineWriteRequest request) {
        return guidelineService.update(currentUserAccessor.require(), id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('LEADER')")
    @Operation(summary = "Excluir estratégia (somente LEADER)")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        guidelineService.delete(currentUserAccessor.require(), id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/history")
    @PreAuthorize("hasRole('LEADER')")
    @Operation(summary = "Histórico append-only da estratégia (somente LEADER)")
    public ApiPage<GuidelineHistoryResponse> history(
            @PathVariable String id,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String direction
    ) {
        return guidelineService.history(id, page, size, sort, direction);
    }
}
