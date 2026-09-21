package br.com.fiap.aguiabranca.dashboard.api;

import java.time.Instant;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.fiap.aguiabranca.dashboard.api.dto.LeaderDashboardResponse;
import br.com.fiap.aguiabranca.dashboard.api.dto.ManagerDashboardResponse;
import br.com.fiap.aguiabranca.dashboard.api.dto.OperatorDashboardResponse;
import br.com.fiap.aguiabranca.dashboard.api.dto.PeriodDashboardResponse;
import br.com.fiap.aguiabranca.dashboard.api.dto.RankingsResponse;
import br.com.fiap.aguiabranca.dashboard.api.dto.StrategiesResponse;
import br.com.fiap.aguiabranca.dashboard.application.DashboardService;
import br.com.fiap.aguiabranca.dashboard.domain.RankingPeriod;
import br.com.fiap.aguiabranca.security.current.CurrentUserAccessor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/dashboard")
@Tag(name = "Dashboard")
@SecurityRequirement(name = "bearer-jwt")
public class DashboardController {

    private final DashboardService dashboardService;
    private final CurrentUserAccessor currentUserAccessor;

    public DashboardController(DashboardService dashboardService, CurrentUserAccessor currentUserAccessor) {
        this.dashboardService = dashboardService;
        this.currentUserAccessor = currentUserAccessor;
    }

    @GetMapping("/operator")
    @PreAuthorize("hasRole('OPERATOR')")
    @Operation(summary = "KPIs do operador logado. MANAGER e LEADER recebem 403. Lista vazia devolve zeros e hasData=false.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Contagens das ideias do operador"),
            @ApiResponse(responseCode = "403", description = "MANAGER ou LEADER", content = @Content)
    })
    public OperatorDashboardResponse operator() {
        return dashboardService.operator(currentUserAccessor.require());
    }

    @GetMapping("/manager")
    @PreAuthorize("hasAnyRole('MANAGER', 'LEADER')")
    @Operation(summary = "Dashboard do gestor. LEADER pode ler. OPERATOR recebe 403. Sem ideias: zeros e hasData=false.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Indicadores do gestor"),
            @ApiResponse(responseCode = "403", description = "OPERATOR", content = @Content)
    })
    public ManagerDashboardResponse manager() {
        return dashboardService.manager();
    }

    @GetMapping("/leader")
    @PreAuthorize("hasRole('LEADER')")
    @Operation(summary = "ROI e totais do portfólio. Números calculados no servidor. OPERATOR e MANAGER recebem 403.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Consolidado do líder"),
            @ApiResponse(responseCode = "403", description = "OPERATOR ou MANAGER", content = @Content)
    })
    public LeaderDashboardResponse leader() {
        return dashboardService.leader();
    }

    @GetMapping("/strategies")
    @PreAuthorize("hasRole('LEADER')")
    @Operation(summary = "Agregado por diretriz. Projetos ou ideias sem diretriz entram em \"Sem estratégia\". from/to opcionais filtram projects.updatedAt.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Uma linha por estratégia"),
            @ApiResponse(responseCode = "400", description = "from posterior a to, ou só um dos limites", content = @Content),
            @ApiResponse(responseCode = "403", description = "OPERATOR ou MANAGER", content = @Content)
    })
    public StrategiesResponse strategies(
            @Parameter(description = "Início ISO-8601 UTC, inclusive. Junto com to, filtra updatedAt.")
            @RequestParam(required = false) Instant from,
            @Parameter(description = "Fim ISO-8601 UTC, inclusive.")
            @RequestParam(required = false) Instant to
    ) {
        return dashboardService.strategies(from, to);
    }

    @GetMapping("/period")
    @PreAuthorize("hasRole('LEADER')")
    @Operation(summary = "Mesmo consolidado do líder, só projetos com createdAt entre from e to. from e to são obrigatórios.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Consolidado do intervalo"),
            @ApiResponse(responseCode = "400", description = "from ou to ausente, ou from posterior a to", content = @Content),
            @ApiResponse(responseCode = "403", description = "OPERATOR ou MANAGER", content = @Content)
    })
    public PeriodDashboardResponse period(
            @RequestParam(required = false) Instant from,
            @RequestParam(required = false) Instant to
    ) {
        return dashboardService.period(from, to);
    }

    @GetMapping("/rankings")
    @PreAuthorize("hasAnyRole('MANAGER', 'LEADER')")
    @Operation(summary = "Ranking de autores com nome e e-mail reais de users. period=MONTH usa o mês corrente em America/Sao_Paulo. Default ALL.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ranking ordenado por ideias enviadas"),
            @ApiResponse(responseCode = "400", description = "period inválido", content = @Content),
            @ApiResponse(responseCode = "403", description = "OPERATOR", content = @Content)
    })
    public RankingsResponse rankings(
            @Parameter(description = "MONTH ou ALL. Default ALL.")
            @RequestParam(required = false, defaultValue = "ALL") RankingPeriod period
    ) {
        return dashboardService.rankings(period);
    }
}
