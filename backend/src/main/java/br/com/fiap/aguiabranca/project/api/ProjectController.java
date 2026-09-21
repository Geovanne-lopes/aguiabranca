package br.com.fiap.aguiabranca.project.api;

import java.time.Instant;

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
import br.com.fiap.aguiabranca.project.api.dto.CreateProjectRequest;
import br.com.fiap.aguiabranca.project.api.dto.ProjectResponse;
import br.com.fiap.aguiabranca.project.api.dto.UpdateProjectRequest;
import br.com.fiap.aguiabranca.project.application.ProjectService;
import br.com.fiap.aguiabranca.project.domain.ProjectStatus;
import br.com.fiap.aguiabranca.security.current.CurrentUserAccessor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/projects")
@Tag(name = "Projects")
@SecurityRequirement(name = "bearer-jwt")
public class ProjectController {

    private final ProjectService projectService;
    private final CurrentUserAccessor currentUserAccessor;

    public ProjectController(ProjectService projectService, CurrentUserAccessor currentUserAccessor) {
        this.projectService = projectService;
        this.currentUserAccessor = currentUserAccessor;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('MANAGER', 'LEADER')")
    @Operation(summary = "Listar projetos (MANAGER e LEADER; OPERATOR 403). O relatório por projeto é GET /projects/{id}.")
    public ApiPage<ProjectResponse> list(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String direction,
            @RequestParam(required = false) ProjectStatus status,
            @RequestParam(required = false) String guidelineId,
            @RequestParam(required = false) String managerId,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Instant from,
            @RequestParam(required = false) Instant to
    ) {
        return projectService.list(page, size, sort, direction, status, guidelineId, managerId, q, from, to);
    }

    @GetMapping("/by-idea/{ideaId}")
    @PreAuthorize("hasAnyRole('MANAGER', 'LEADER')")
    @Operation(summary = "Obter o projeto de uma ideia. 404 se a ideia não tem projeto.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Projeto da ideia"),
            @ApiResponse(responseCode = "403", description = "OPERATOR", content = @Content),
            @ApiResponse(responseCode = "404", description = "Ideia sem projeto", content = @Content)
    })
    public ProjectResponse getByIdeaId(@PathVariable String ideaId) {
        return projectService.getByIdeaId(ideaId);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('MANAGER', 'LEADER')")
    @Operation(summary = "Obter projeto, incluindo ROI calculado. Também é o relatório por projeto.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Projeto"),
            @ApiResponse(responseCode = "403", description = "OPERATOR", content = @Content),
            @ApiResponse(responseCode = "404", description = "Projeto inexistente", content = @Content)
    })
    public ProjectResponse getById(@PathVariable String id) {
        return projectService.getById(id);
    }

    @PostMapping
    @PreAuthorize("hasRole('MANAGER')")
    @Operation(summary = "Criar projeto a partir de ideia APPROVED ou PRIORITIZED (somente MANAGER)")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Projeto BACKLOG"),
            @ApiResponse(responseCode = "403", description = "LEADER ou OPERATOR", content = @Content),
            @ApiResponse(responseCode = "404", description = "Ideia inexistente", content = @Content),
            @ApiResponse(responseCode = "409", description = "IDEA_ALREADY_HAS_PROJECT", content = @Content),
            @ApiResponse(responseCode = "422", description = "Ideia não está APPROVED nem PRIORITIZED", content = @Content)
    })
    public ResponseEntity<ProjectResponse> create(@Valid @RequestBody CreateProjectRequest request) {
        ProjectResponse created = projectService.create(currentUserAccessor.require(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    @Operation(summary = "Atualizar projeto (somente MANAGER). Não altera ideaId, managerId nem createdAt.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Projeto atualizado, com roiPercent calculado"),
            @ApiResponse(responseCode = "400", description = "Números inválidos", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "403", description = "LEADER ou OPERATOR", content = @Content),
            @ApiResponse(responseCode = "404", description = "Projeto inexistente", content = @Content),
            @ApiResponse(responseCode = "422", description = "Diretriz inexistente", content = @Content)
    })
    public ProjectResponse update(@PathVariable String id, @Valid @RequestBody UpdateProjectRequest request) {
        return projectService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    @Operation(summary = "Excluir projeto (somente MANAGER). A ideia permanece.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Projeto removido"),
            @ApiResponse(responseCode = "403", description = "LEADER ou OPERATOR", content = @Content),
            @ApiResponse(responseCode = "404", description = "Projeto inexistente", content = @Content)
    })
    public ResponseEntity<Void> delete(@PathVariable String id) {
        projectService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
