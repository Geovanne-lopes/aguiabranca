package br.com.fiap.aguiabranca.idea.api;

import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.fiap.aguiabranca.common.api.ApiPage;
import br.com.fiap.aguiabranca.idea.api.dto.CreateIdeaRequest;
import br.com.fiap.aguiabranca.idea.api.dto.IdeaResponse;
import br.com.fiap.aguiabranca.idea.api.dto.UpdateIdeaRequest;
import br.com.fiap.aguiabranca.idea.api.dto.UpdateIdeaStatusRequest;
import br.com.fiap.aguiabranca.idea.application.IdeaService;
import br.com.fiap.aguiabranca.idea.domain.IdeaCategory;
import br.com.fiap.aguiabranca.idea.domain.IdeaStatus;
import br.com.fiap.aguiabranca.security.current.CurrentUserAccessor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/ideas")
@Tag(name = "Ideas")
@SecurityRequirement(name = "bearer-jwt")
public class IdeaController {

    private final IdeaService ideaService;
    private final CurrentUserAccessor currentUserAccessor;

    public IdeaController(IdeaService ideaService, CurrentUserAccessor currentUserAccessor) {
        this.ideaService = ideaService;
        this.currentUserAccessor = currentUserAccessor;
    }

    @GetMapping
    @Operation(summary = "Listar ideias no escopo do papel")
    public ApiPage<IdeaResponse> list(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String direction,
            @RequestParam(required = false) IdeaStatus status,
            @RequestParam(required = false) IdeaCategory category,
            @RequestParam(required = false) String guidelineId,
            @RequestParam(required = false) String authorId,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Instant from,
            @RequestParam(required = false) Instant to,
            @RequestParam(required = false) Boolean mine
    ) {
        return ideaService.list(
                currentUserAccessor.require(),
                page,
                size,
                sort,
                direction,
                status,
                category,
                guidelineId,
                authorId,
                q,
                from,
                to,
                mine
        );
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('OPERATOR', 'MANAGER')")
    @Operation(summary = "Criar ideia (OPERATOR e MANAGER; LEADER 403)")
    public ResponseEntity<IdeaResponse> create(@Valid @RequestBody CreateIdeaRequest request) {
        IdeaResponse created = ideaService.create(currentUserAccessor.require(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obter ideia. OPERATOR recebe 404 se não for o autor")
    public IdeaResponse getById(@PathVariable String id) {
        return ideaService.getById(currentUserAccessor.require(), id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('OPERATOR')")
    @Operation(summary = "Atualizar ideia PENDING do próprio autor. Outro autor: 404. Fora de PENDING: 422. Com projeto: 409.")
    public IdeaResponse update(@PathVariable String id, @Valid @RequestBody UpdateIdeaRequest request) {
        return ideaService.update(currentUserAccessor.require(), id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('OPERATOR')")
    @Operation(summary = "Excluir ideia PENDING do próprio autor. Outro autor: 404. Fora de PENDING: 422. Com projeto: 409.")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        ideaService.delete(currentUserAccessor.require(), id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('MANAGER')")
    @Operation(summary = "Alterar status da ideia (somente MANAGER)")
    public IdeaResponse changeStatus(@PathVariable String id, @Valid @RequestBody UpdateIdeaStatusRequest request) {
        return ideaService.changeStatus(currentUserAccessor.require(), id, request);
    }
}
