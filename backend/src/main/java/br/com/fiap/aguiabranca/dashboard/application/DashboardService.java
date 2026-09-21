package br.com.fiap.aguiabranca.dashboard.application;

import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;

import br.com.fiap.aguiabranca.common.api.ApiErrorDetail;
import br.com.fiap.aguiabranca.common.exception.ApiException;
import br.com.fiap.aguiabranca.dashboard.api.dto.LeaderDashboardResponse;
import br.com.fiap.aguiabranca.dashboard.api.dto.ManagerDashboardResponse;
import br.com.fiap.aguiabranca.dashboard.api.dto.MonthBarResponse;
import br.com.fiap.aguiabranca.dashboard.api.dto.OperatorDashboardResponse;
import br.com.fiap.aguiabranca.dashboard.api.dto.PeriodDashboardResponse;
import br.com.fiap.aguiabranca.dashboard.api.dto.RankingItemResponse;
import br.com.fiap.aguiabranca.dashboard.api.dto.RankingsResponse;
import br.com.fiap.aguiabranca.dashboard.api.dto.StatusCountResponse;
import br.com.fiap.aguiabranca.dashboard.api.dto.StrategiesResponse;
import br.com.fiap.aguiabranca.dashboard.api.dto.StrategyItemResponse;
import br.com.fiap.aguiabranca.dashboard.application.InsightDataset.StrategyBrief;
import br.com.fiap.aguiabranca.dashboard.domain.MonthTrend;
import br.com.fiap.aguiabranca.dashboard.domain.OperatorKpis;
import br.com.fiap.aguiabranca.dashboard.domain.OperatorKpis.IdeaPoint;
import br.com.fiap.aguiabranca.dashboard.domain.PortfolioMetrics;
import br.com.fiap.aguiabranca.dashboard.domain.PortfolioSummary;
import br.com.fiap.aguiabranca.dashboard.domain.ProjectFigures;
import br.com.fiap.aguiabranca.dashboard.domain.RankedAuthor;
import br.com.fiap.aguiabranca.dashboard.domain.RankingPeriod;
import br.com.fiap.aguiabranca.dashboard.domain.RankingSorter;
import br.com.fiap.aguiabranca.dashboard.domain.SaoPauloCalendar;
import br.com.fiap.aguiabranca.dashboard.domain.StatusLabels;
import br.com.fiap.aguiabranca.guideline.infra.GuidelineDocument;
import br.com.fiap.aguiabranca.guideline.infra.GuidelineRepository;
import br.com.fiap.aguiabranca.idea.domain.IdeaStatus;
import br.com.fiap.aguiabranca.idea.infra.DecisionHistoryItem;
import br.com.fiap.aguiabranca.idea.infra.IdeaDocument;
import br.com.fiap.aguiabranca.idea.infra.IdeaRepository;
import br.com.fiap.aguiabranca.project.domain.ProjectStatus;
import br.com.fiap.aguiabranca.project.infra.ProjectDocument;
import br.com.fiap.aguiabranca.project.infra.ProjectRepository;
import br.com.fiap.aguiabranca.security.current.CurrentUser;
import br.com.fiap.aguiabranca.user.infra.UserDocument;
import br.com.fiap.aguiabranca.user.infra.UserRepository;

@Service
public class DashboardService {

    static final String UNASSIGNED_STRATEGY = "Sem estratégia";

    private final IdeaRepository ideaRepository;
    private final ProjectRepository projectRepository;
    private final GuidelineRepository guidelineRepository;
    private final UserRepository userRepository;
    private final Clock clock;

    public DashboardService(
            IdeaRepository ideaRepository,
            ProjectRepository projectRepository,
            GuidelineRepository guidelineRepository,
            UserRepository userRepository,
            Clock clock
    ) {
        this.ideaRepository = ideaRepository;
        this.projectRepository = projectRepository;
        this.guidelineRepository = guidelineRepository;
        this.userRepository = userRepository;
        this.clock = clock;
    }

    public OperatorDashboardResponse operator(CurrentUser user) {
        List<IdeaPoint> points = ideaRepository.findAll().stream()
                .filter(idea -> user.id().equals(idea.getAuthorId()))
                .map(DashboardService::toPoint)
                .toList();
        OperatorKpis.Result result = OperatorKpis.compute(points, clock);
        return new OperatorDashboardResponse(
                result.ideasSubmittedCount(),
                result.ideasApprovedCount(),
                result.ideasPrioritizedCount(),
                result.ideasRejectedCount(),
                result.ideasPendingCount(),
                result.submittedThisMonth(),
                result.submittedPreviousMonth(),
                result.submittedTrendPercent(),
                result.submittedTrendLabel(),
                result.approvedThisMonth(),
                result.approvedPreviousMonth(),
                result.approvedTrendLabel(),
                result.hasData()
        );
    }

    public ManagerDashboardResponse manager() {
        List<IdeaDocument> ideas = ideaRepository.findAll();
        List<Instant> createdAts = ideas.stream().map(IdeaDocument::getCreatedAt).toList();
        Instant currentStart = SaoPauloCalendar.startOfMonth(clock);
        Instant nextStart = SaoPauloCalendar.startOfNextMonth(clock);
        Instant previousStart = SaoPauloCalendar.startOfPreviousMonth(clock);
        int thisMonth = SaoPauloCalendar.countInMonth(createdAts, currentStart, nextStart);
        int previousMonth = SaoPauloCalendar.countInMonth(createdAts, previousStart, currentStart);
        int pending = 0;
        int approvedOrPrioritized = 0;
        Map<IdeaStatus, Integer> byStatus = new EnumMap<>(IdeaStatus.class);
        for (IdeaStatus status : IdeaStatus.values()) {
            byStatus.put(status, 0);
        }
        for (IdeaDocument idea : ideas) {
            IdeaStatus status = idea.getStatus() == null ? IdeaStatus.PENDING : idea.getStatus();
            byStatus.merge(status, 1, Integer::sum);
            if (status == IdeaStatus.PENDING) {
                pending++;
            }
            if (status == IdeaStatus.APPROVED || status == IdeaStatus.PRIORITIZED) {
                approvedOrPrioritized++;
            }
        }
        int approvalRate = MonthTrend.approvalRatePercent(approvedOrPrioritized, ideas.size());
        PortfolioSummary portfolio = PortfolioMetrics.summarize(figures(projectRepository.findAll()));
        List<MonthBarResponse> bars = SaoPauloCalendar.lastMonths(createdAts, clock, 5).stream()
                .map(bar -> new MonthBarResponse(bar.label(), bar.count()))
                .toList();
        List<StatusCountResponse> ideasByStatus = new ArrayList<>();
        for (IdeaStatus status : IdeaStatus.values()) {
            ideasByStatus.add(new StatusCountResponse(status.name(), StatusLabels.idea(status), byStatus.get(status)));
        }
        return new ManagerDashboardResponse(
                pending,
                portfolio.activeProjectsCount(),
                thisMonth,
                previousMonth,
                MonthTrend.label(thisMonth, previousMonth),
                approvalRate,
                MonthTrend.approvalRateLabel(ideas.size(), approvalRate),
                bars,
                ideasByStatus,
                !ideas.isEmpty()
        );
    }

    public LeaderDashboardResponse leader() {
        return toLeader(figures(projectRepository.findAll()));
    }

    public PeriodDashboardResponse period(Instant from, Instant to) {
        Range range = Range.required(from, to);
        List<ProjectFigures> projects = figures(projectRepository.findAll()).stream()
                .filter(project -> createdIn(project, range))
                .toList();
        return PeriodDashboardResponse.of(toLeader(projects), range.from(), range.to());
    }

    public StrategiesResponse strategies(Instant from, Instant to) {
        Range range = Range.optional(from, to);
        List<ProjectDocument> projects = projectRepository.findAll().stream()
                .filter(project -> updatedIn(project, range))
                .toList();
        return new StrategiesResponse(strategyItems(projects, ideaRepository.findAll()));
    }

    public RankingsResponse rankings(RankingPeriod period) {
        RankingPeriod selected = period == null ? RankingPeriod.ALL : period;
        Instant monthStart = selected == RankingPeriod.MONTH ? SaoPauloCalendar.startOfMonth(clock) : null;
        Map<String, List<IdeaDocument>> byAuthor = new HashMap<>();
        for (IdeaDocument idea : ideaRepository.findAll()) {
            if (idea.getAuthorId() == null || idea.getAuthorId().isBlank()) {
                continue;
            }
            if (monthStart != null && (idea.getCreatedAt() == null || idea.getCreatedAt().isBefore(monthStart))) {
                continue;
            }
            byAuthor.computeIfAbsent(idea.getAuthorId(), ignored -> new ArrayList<>()).add(idea);
        }
        Map<String, UserDocument> users = new HashMap<>();
        userRepository.findAllById(byAuthor.keySet()).forEach(user -> users.put(user.getId(), user));
        List<RankedAuthor> authors = new ArrayList<>();
        for (Map.Entry<String, List<IdeaDocument>> entry : byAuthor.entrySet()) {
            UserDocument user = users.get(entry.getKey());
            if (user == null) {
                continue;
            }
            int approved = 0;
            for (IdeaDocument idea : entry.getValue()) {
                if (idea.getStatus() == IdeaStatus.APPROVED || idea.getStatus() == IdeaStatus.PRIORITIZED) {
                    approved++;
                }
            }
            authors.add(new RankedAuthor(
                    user.getId(),
                    user.getName() == null ? "" : user.getName(),
                    user.getEmail() == null ? "" : user.getEmail(),
                    entry.getValue().size(),
                    approved
            ));
        }
        List<RankingItemResponse> items = RankingSorter.sort(authors).stream()
                .map(author -> new RankingItemResponse(
                        author.authorId(),
                        author.name(),
                        author.email(),
                        author.ideasSubmitted(),
                        author.ideasApproved()
                ))
                .toList();
        return new RankingsResponse(items);
    }

    public InsightDataset insightDataset(Instant from, Instant to) {
        Range range = Range.optional(from, to);
        List<ProjectDocument> projects = projectRepository.findAll().stream()
                .filter(project -> createdIn(project, range))
                .toList();
        List<IdeaDocument> ideas = ideaRepository.findAll().stream()
                .filter(idea -> createdIn(idea.getCreatedAt(), range))
                .toList();
        List<ProjectFigures> figures = figures(projects);
        int pending = 0;
        int approved = 0;
        int rejected = 0;
        for (IdeaDocument idea : ideas) {
            IdeaStatus status = idea.getStatus() == null ? IdeaStatus.PENDING : idea.getStatus();
            if (status == IdeaStatus.PENDING) {
                pending++;
            } else if (status == IdeaStatus.APPROVED || status == IdeaStatus.PRIORITIZED) {
                approved++;
            } else if (status == IdeaStatus.REJECTED) {
                rejected++;
            }
        }
        List<StrategyBrief> briefs = strategyItems(projects, ideas).stream()
                .map(item -> new StrategyBrief(item.guidelineTitle(), item.projectsCount(), item.overallRoiPercent()))
                .toList();
        return new InsightDataset(
                PortfolioMetrics.summarize(figures),
                statusCounts(figures),
                pending,
                approved,
                rejected,
                briefs
        );
    }

    private LeaderDashboardResponse toLeader(List<ProjectFigures> projects) {
        PortfolioSummary summary = PortfolioMetrics.summarize(projects);
        List<StatusCountResponse> byStatus = new ArrayList<>();
        Map<String, Integer> counts = statusCounts(projects);
        for (ProjectStatus status : ProjectStatus.values()) {
            byStatus.add(new StatusCountResponse(
                    status.name(),
                    StatusLabels.project(status),
                    counts.getOrDefault(status.name(), 0)
            ));
        }
        return new LeaderDashboardResponse(
                summary.totalProjects(),
                summary.activeProjectsCount(),
                summary.completedProjectsCount(),
                summary.totalInvestment(),
                summary.totalObtainedProfit(),
                summary.overallRoiPercent(),
                summary.averageProductivityGainPercent(),
                summary.averageDeadlineDays(),
                byStatus,
                summary.hasData()
        );
    }

    private List<StrategyItemResponse> strategyItems(List<ProjectDocument> projects, List<IdeaDocument> ideas) {
        Map<String, String> titles = new HashMap<>();
        for (GuidelineDocument guideline : guidelineRepository.findAll()) {
            titles.put(guideline.getId(), guideline.getTitle());
        }
        Map<String, List<ProjectDocument>> projectsByGuideline = new HashMap<>();
        Map<String, Integer> ideasByGuideline = new HashMap<>();
        for (ProjectDocument project : projects) {
            projectsByGuideline.computeIfAbsent(guidelineKey(project.getGuidelineId()), ignored -> new ArrayList<>()).add(project);
        }
        for (IdeaDocument idea : ideas) {
            String key = guidelineKey(idea.getGuidelineId());
            ideasByGuideline.merge(key, 1, Integer::sum);
        }
        List<StrategyItemResponse> items = new ArrayList<>();
        for (String key : union(projectsByGuideline.keySet(), ideasByGuideline.keySet())) {
            List<ProjectDocument> bucket = projectsByGuideline.getOrDefault(key, List.of());
            int ideaCount = ideasByGuideline.getOrDefault(key, 0);
            if (bucket.isEmpty() && ideaCount == 0) {
                continue;
            }
            boolean known = key != null && titles.containsKey(key);
            String title = known ? titles.get(key) : UNASSIGNED_STRATEGY;
            String guidelineId = known ? key : null;
            PortfolioSummary summary = PortfolioMetrics.summarize(figures(bucket));
            items.add(new StrategyItemResponse(
                    guidelineId,
                    title,
                    ideaCount,
                    summary.totalProjects(),
                    summary.totalInvestment(),
                    summary.totalObtainedProfit(),
                    summary.overallRoiPercent(),
                    summary.averageProductivityGainPercent(),
                    summary.completedProjectsCount()
            ));
        }
        items.sort(Comparator
                .comparing((StrategyItemResponse item) -> UNASSIGNED_STRATEGY.equals(item.guidelineTitle()))
                .thenComparing(StrategyItemResponse::guidelineTitle, String.CASE_INSENSITIVE_ORDER));
        return mergeUnassigned(items);
    }

    private static List<StrategyItemResponse> mergeUnassigned(List<StrategyItemResponse> items) {
        List<StrategyItemResponse> named = new ArrayList<>();
        int ideas = 0;
        boolean hasOrphan = false;
        double investment = 0;
        double profit = 0;
        double productivityWeighted = 0;
        int projects = 0;
        int completed = 0;
        for (StrategyItemResponse item : items) {
            if (!UNASSIGNED_STRATEGY.equals(item.guidelineTitle())) {
                named.add(item);
                continue;
            }
            hasOrphan = true;
            ideas += item.ideasCount();
            projects += item.projectsCount();
            investment += item.totalInvestment();
            profit += item.totalObtainedProfit();
            productivityWeighted += item.averageProductivityGainPercent() * item.projectsCount();
            completed += item.completedProjectsCount();
        }
        if (!hasOrphan) {
            return named;
        }
        double averageProductivity = projects == 0 ? 0.0 : productivityWeighted / projects;
        named.add(new StrategyItemResponse(
                null,
                UNASSIGNED_STRATEGY,
                ideas,
                projects,
                investment,
                profit,
                br.com.fiap.aguiabranca.project.domain.ProjectRoi.percent(investment, profit),
                averageProductivity,
                completed
        ));
        return named;
    }

    private static Map<String, Integer> statusCounts(List<ProjectFigures> projects) {
        Map<String, Integer> counts = new LinkedHashMap<>();
        for (ProjectStatus status : ProjectStatus.values()) {
            counts.put(status.name(), 0);
        }
        for (ProjectFigures project : projects) {
            ProjectStatus status = project.status() == null ? ProjectStatus.BACKLOG : project.status();
            counts.merge(status.name(), 1, Integer::sum);
        }
        return counts;
    }

    private static List<ProjectFigures> figures(List<ProjectDocument> projects) {
        List<ProjectFigures> figures = new ArrayList<>(projects.size());
        for (ProjectDocument project : projects) {
            figures.add(new ProjectFigures(
                    project.getInvestmentAmount(),
                    project.getObtainedProfit(),
                    project.getProductivityGainPercent(),
                    project.getStatus() == null ? ProjectStatus.BACKLOG : project.getStatus(),
                    project.getCreatedAt(),
                    project.getDeadline(),
                    guidelineKey(project.getGuidelineId())
            ));
        }
        return figures;
    }

    private static IdeaPoint toPoint(IdeaDocument idea) {
        return new IdeaPoint(
                idea.getStatus() == null ? IdeaStatus.PENDING : idea.getStatus(),
                idea.getCreatedAt(),
                firstApprovalAt(idea.getDecisionHistory())
        );
    }

    static Instant firstApprovalAt(List<DecisionHistoryItem> history) {
        if (history == null || history.isEmpty()) {
            return null;
        }
        Instant earliest = null;
        for (DecisionHistoryItem item : history) {
            if (item == null || item.getOccurredAt() == null) {
                continue;
            }
            if (item.getStatus() != IdeaStatus.APPROVED && item.getStatus() != IdeaStatus.PRIORITIZED) {
                continue;
            }
            if (earliest == null || item.getOccurredAt().isBefore(earliest)) {
                earliest = item.getOccurredAt();
            }
        }
        return earliest;
    }

    private static String guidelineKey(String guidelineId) {
        if (guidelineId == null || guidelineId.isBlank()) {
            return null;
        }
        return guidelineId;
    }

    private static boolean createdIn(ProjectDocument project, Range range) {
        return createdIn(project.getCreatedAt(), range);
    }

    private static boolean createdIn(ProjectFigures project, Range range) {
        return createdIn(project.createdAt(), range);
    }

    private static boolean updatedIn(ProjectDocument project, Range range) {
        if (range == null) {
            return true;
        }
        return createdIn(project.getUpdatedAt(), range);
    }

    private static boolean createdIn(Instant value, Range range) {
        if (range == null) {
            return true;
        }
        return value != null && !value.isBefore(range.from()) && !value.isAfter(range.to());
    }

    private static List<String> union(Set<String> left, Set<String> right) {
        Set<String> seen = new LinkedHashSet<>();
        List<String> keys = new ArrayList<>();
        for (String key : left) {
            if (seen.add(key)) {
                keys.add(key);
            }
        }
        for (String key : right) {
            if (seen.add(key)) {
                keys.add(key);
            }
        }
        return keys;
    }

    private record Range(Instant from, Instant to) {

        static Range required(Instant from, Instant to) {
            if (from == null || to == null) {
                List<ApiErrorDetail> details = new ArrayList<>();
                if (from == null) {
                    details.add(new ApiErrorDetail("from", "é obrigatório"));
                }
                if (to == null) {
                    details.add(new ApiErrorDetail("to", "é obrigatório"));
                }
                throw ApiException.validation("Um ou mais campos são inválidos.", details);
            }
            return closed(from, to);
        }

        static Range optional(Instant from, Instant to) {
            if (from == null && to == null) {
                return null;
            }
            if (from == null || to == null) {
                String missing = from == null ? "from" : "to";
                throw ApiException.validation(
                        "Um ou mais campos são inválidos.",
                        List.of(new ApiErrorDetail(missing, "é obrigatório quando o outro limite é informado"))
                );
            }
            return closed(from, to);
        }

        private static Range closed(Instant from, Instant to) {
            if (from.isAfter(to)) {
                throw ApiException.validation(
                        "Um ou mais campos são inválidos.",
                        List.of(new ApiErrorDetail("from", "deve ser anterior ou igual a to"))
                );
            }
            return new Range(from, to);
        }
    }
}
