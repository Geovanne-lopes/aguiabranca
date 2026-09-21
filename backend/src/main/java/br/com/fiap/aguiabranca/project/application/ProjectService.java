package br.com.fiap.aguiabranca.project.application;

import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import br.com.fiap.aguiabranca.common.api.ApiErrorDetail;
import br.com.fiap.aguiabranca.common.api.ApiPage;
import br.com.fiap.aguiabranca.common.api.Pagination;
import br.com.fiap.aguiabranca.common.exception.ApiException;
import br.com.fiap.aguiabranca.common.exception.BusinessException;
import br.com.fiap.aguiabranca.common.exception.NotFoundException;
import br.com.fiap.aguiabranca.common.persistence.MongoPages;
import br.com.fiap.aguiabranca.guideline.infra.GuidelineRepository;
import br.com.fiap.aguiabranca.idea.domain.IdeaStatus;
import br.com.fiap.aguiabranca.idea.infra.IdeaDocument;
import br.com.fiap.aguiabranca.idea.infra.IdeaRepository;
import br.com.fiap.aguiabranca.project.api.dto.CreateProjectRequest;
import br.com.fiap.aguiabranca.project.api.dto.ProjectResponse;
import br.com.fiap.aguiabranca.project.api.dto.UpdateProjectRequest;
import br.com.fiap.aguiabranca.project.domain.ProjectRoi;
import br.com.fiap.aguiabranca.project.domain.ProjectStatus;
import br.com.fiap.aguiabranca.project.infra.ProjectDocument;
import br.com.fiap.aguiabranca.project.infra.ProjectRepository;
import br.com.fiap.aguiabranca.security.current.CurrentUser;
import br.com.fiap.aguiabranca.user.infra.UserDocument;
import br.com.fiap.aguiabranca.user.infra.UserRepository;

@Service
public class ProjectService {

    static final Set<String> LIST_SORT_FIELDS = Set.of("updatedAt", "createdAt", "title", "status");
    private static final String REMOVED_GUIDELINE_TITLE = "Estratégia removida";

    private final ProjectRepository projectRepository;
    private final IdeaRepository ideaRepository;
    private final GuidelineRepository guidelineRepository;
    private final UserRepository userRepository;
    private final MongoTemplate mongoTemplate;
    private final Clock clock;

    public ProjectService(
            ProjectRepository projectRepository,
            IdeaRepository ideaRepository,
            GuidelineRepository guidelineRepository,
            UserRepository userRepository,
            MongoTemplate mongoTemplate,
            Clock clock
    ) {
        this.projectRepository = projectRepository;
        this.ideaRepository = ideaRepository;
        this.guidelineRepository = guidelineRepository;
        this.userRepository = userRepository;
        this.mongoTemplate = mongoTemplate;
        this.clock = clock;
    }

    public ApiPage<ProjectResponse> list(
            Integer page,
            Integer size,
            String sort,
            String direction,
            ProjectStatus status,
            String guidelineId,
            String managerId,
            String q,
            Instant from,
            Instant to
    ) {
        PageRequest pageable = Pagination.of(page, size, sort, direction, LIST_SORT_FIELDS, "updatedAt");
        Query query = new Query();
        List<Criteria> criteria = new ArrayList<>();
        if (status != null) {
            criteria.add(Criteria.where("status").is(status));
        }
        if (guidelineId != null && !guidelineId.isBlank()) {
            criteria.add(Criteria.where("guidelineId").is(guidelineId.trim()));
        }
        if (managerId != null && !managerId.isBlank()) {
            criteria.add(Criteria.where("managerId").is(managerId.trim()));
        }
        if (q != null && !q.isBlank()) {
            String regex = Pattern.quote(q.trim());
            criteria.add(new Criteria().orOperator(
                    Criteria.where("title").regex(regex, "i"),
                    Criteria.where("description").regex(regex, "i")
            ));
        }
        if (from != null || to != null) {
            Criteria updatedAt = Criteria.where("updatedAt");
            if (from != null) {
                updatedAt.gte(from);
            }
            if (to != null) {
                updatedAt.lte(to);
            }
            criteria.add(updatedAt);
        }
        if (criteria.size() == 1) {
            query.addCriteria(criteria.get(0));
        } else if (criteria.size() > 1) {
            query.addCriteria(new Criteria().andOperator(criteria));
        }

        Page<ProjectDocument> result = MongoPages.find(mongoTemplate, query, pageable, ProjectDocument.class);
        return ApiPage.of(toResponses(result.getContent()), result);
    }

    public ProjectResponse getById(String id) {
        return toResponse(require(id));
    }

    public ProjectResponse getByIdeaId(String ideaId) {
        return projectRepository.findByIdeaId(ideaId)
                .map(this::toResponse)
                .orElseThrow(NotFoundException::new);
    }

    public ProjectResponse create(CurrentUser manager, CreateProjectRequest request) {
        IdeaDocument idea = ideaRepository.findById(request.ideaId()).orElseThrow(NotFoundException::new);
        if (idea.getStatus() != IdeaStatus.APPROVED && idea.getStatus() != IdeaStatus.PRIORITIZED) {
            throw new BusinessException("Somente ideias aprovadas ou priorizadas podem originar um projeto.");
        }
        if (projectRepository.existsByIdeaId(idea.getId())) {
            throw ApiException.ideaAlreadyLinkedToProject();
        }

        ProjectDocument project = new ProjectDocument();
        project.setId(UUID.randomUUID().toString());
        project.setIdeaId(idea.getId());
        project.setGuidelineId(idea.getGuidelineId());
        project.setTitle(idea.getTitle());
        project.setDescription(idea.getDescription());
        project.setStatus(ProjectStatus.BACKLOG);
        project.setInvestmentAmount(0);
        project.setObtainedProfit(0);
        project.setProductivityGainPercent(0);
        project.setManagerId(manager.id());
        Instant now = Instant.now(clock).truncatedTo(ChronoUnit.MILLIS);
        project.setCreatedAt(now);
        project.setUpdatedAt(now);
        try {
            return toResponse(projectRepository.save(project));
        } catch (DuplicateKeyException ex) {
            throw ApiException.ideaAlreadyLinkedToProject();
        }
    }

    public ProjectResponse update(String id, UpdateProjectRequest request) {
        requireFinite("investmentAmount", request.investmentAmount(), 0, Double.POSITIVE_INFINITY);
        requireFinite("obtainedProfit", request.obtainedProfit(), 0, Double.POSITIVE_INFINITY);
        requireFinite("productivityGainPercent", request.productivityGainPercent(), 0, 100);

        ProjectDocument project = require(id);
        project.setTitle(request.title());
        project.setDescription(request.description());
        project.setStatus(request.status());
        project.setInvestmentAmount(request.investmentAmount());
        project.setObtainedProfit(request.obtainedProfit());
        project.setProductivityGainPercent(request.productivityGainPercent());
        project.setDeadline(request.deadline());
        project.setGuidelineId(resolveGuidelineId(request.guidelineId()));
        return toResponse(projectRepository.save(project));
    }

    public void delete(String id) {
        if (!projectRepository.existsById(id)) {
            throw new NotFoundException();
        }
        projectRepository.deleteById(id);
    }

    private ProjectDocument require(String id) {
        return projectRepository.findById(id).orElseThrow(NotFoundException::new);
    }

    private String resolveGuidelineId(String guidelineId) {
        if (guidelineId == null) {
            return null;
        }
        if (!guidelineRepository.existsById(guidelineId)) {
            throw new BusinessException("Diretriz informada não existe.");
        }
        return guidelineId;
    }

    private static void requireFinite(String field, Double value, double min, double max) {
        boolean aboveMax = value != null && max != Double.POSITIVE_INFINITY && value > max;
        if (value == null || !Double.isFinite(value) || value < min || aboveMax) {
            String issue = max == 100
                    ? "deve estar entre 0 e 100"
                    : "deve ser um número finito maior ou igual a zero";
            throw ApiException.validation(
                    "Um ou mais campos são inválidos.",
                    List.of(new ApiErrorDetail(field, issue))
            );
        }
    }

    private ProjectResponse toResponse(ProjectDocument project) {
        return toResponses(List.of(project)).get(0);
    }

    private List<ProjectResponse> toResponses(List<ProjectDocument> projects) {
        Map<String, String> names = namesById(projects.stream().map(ProjectDocument::getManagerId).toList());
        Map<String, String> titles = guidelineTitles(projects);
        return projects.stream().map(project -> toResponse(project, names, titles)).toList();
    }

    private ProjectResponse toResponse(ProjectDocument project, Map<String, String> names, Map<String, String> titles) {
        return new ProjectResponse(
                project.getId(),
                project.getIdeaId(),
                project.getGuidelineId(),
                guidelineTitle(project.getGuidelineId(), titles),
                project.getTitle(),
                project.getDescription(),
                project.getStatus(),
                project.getInvestmentAmount(),
                project.getObtainedProfit(),
                project.getProductivityGainPercent(),
                ProjectRoi.percent(project.getInvestmentAmount(), project.getObtainedProfit()),
                project.getDeadline(),
                project.getManagerId(),
                names.get(project.getManagerId()),
                project.getCreatedAt(),
                project.getUpdatedAt()
        );
    }

    private String guidelineTitle(String guidelineId, Map<String, String> titles) {
        if (guidelineId == null) {
            return null;
        }
        return titles.getOrDefault(guidelineId, REMOVED_GUIDELINE_TITLE);
    }

    private Map<String, String> namesById(List<String> ids) {
        List<String> distinct = ids.stream().filter(id -> id != null && !id.isBlank()).distinct().toList();
        if (distinct.isEmpty()) {
            return Map.of();
        }
        return StreamSupport.stream(userRepository.findAllById(distinct).spliterator(), false)
                .collect(Collectors.toMap(UserDocument::getId, UserDocument::getName, (left, right) -> left));
    }

    private Map<String, String> guidelineTitles(List<ProjectDocument> projects) {
        List<String> ids = projects.stream()
                .map(ProjectDocument::getGuidelineId)
                .filter(id -> id != null && !id.isBlank())
                .distinct()
                .toList();
        if (ids.isEmpty()) {
            return Map.of();
        }
        Map<String, String> titles = new HashMap<>();
        guidelineRepository.findAllById(ids).forEach(guideline -> titles.put(guideline.getId(), guideline.getTitle()));
        return titles;
    }
}
