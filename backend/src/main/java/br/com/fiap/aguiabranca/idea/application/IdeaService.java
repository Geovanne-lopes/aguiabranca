package br.com.fiap.aguiabranca.idea.application;

import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
import br.com.fiap.aguiabranca.guideline.infra.GuidelineDocument;
import br.com.fiap.aguiabranca.guideline.infra.GuidelineRepository;
import br.com.fiap.aguiabranca.idea.api.dto.CreateIdeaRequest;
import br.com.fiap.aguiabranca.idea.api.dto.DecisionHistoryResponse;
import br.com.fiap.aguiabranca.idea.api.dto.IdeaResponse;
import br.com.fiap.aguiabranca.idea.api.dto.UpdateIdeaRequest;
import br.com.fiap.aguiabranca.idea.api.dto.UpdateIdeaStatusRequest;
import br.com.fiap.aguiabranca.idea.domain.IdeaCategory;
import br.com.fiap.aguiabranca.idea.domain.IdeaLifecycle;
import br.com.fiap.aguiabranca.idea.domain.IdeaStatus;
import br.com.fiap.aguiabranca.idea.infra.DecisionHistoryItem;
import br.com.fiap.aguiabranca.idea.infra.IdeaDocument;
import br.com.fiap.aguiabranca.idea.infra.IdeaRepository;
import br.com.fiap.aguiabranca.project.infra.ProjectRepository;
import br.com.fiap.aguiabranca.security.current.CurrentUser;
import br.com.fiap.aguiabranca.user.domain.UserRole;
import br.com.fiap.aguiabranca.user.infra.UserDocument;
import br.com.fiap.aguiabranca.user.infra.UserRepository;

@Service
public class IdeaService {

    static final Set<String> LIST_SORT_FIELDS = Set.of("createdAt", "updatedAt", "title", "status");
    private static final String REMOVED_GUIDELINE_TITLE = "Estratégia removida";

    private final IdeaRepository ideaRepository;
    private final GuidelineRepository guidelineRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final MongoTemplate mongoTemplate;
    private final Clock clock;

    public IdeaService(
            IdeaRepository ideaRepository,
            GuidelineRepository guidelineRepository,
            UserRepository userRepository,
            ProjectRepository projectRepository,
            MongoTemplate mongoTemplate,
            Clock clock
    ) {
        this.ideaRepository = ideaRepository;
        this.guidelineRepository = guidelineRepository;
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
        this.mongoTemplate = mongoTemplate;
        this.clock = clock;
    }

    public ApiPage<IdeaResponse> list(
            CurrentUser user,
            Integer page,
            Integer size,
            String sort,
            String direction,
            IdeaStatus status,
            IdeaCategory category,
            String guidelineId,
            String authorId,
            String q,
            Instant from,
            Instant to,
            Boolean mine
    ) {
        PageRequest pageable = Pagination.of(page, size, sort, direction, LIST_SORT_FIELDS, "createdAt");
        Query query = new Query();
        List<Criteria> criteria = new ArrayList<>();
        applyAuthorScope(user, authorId, mine, criteria);
        if (status != null) {
            criteria.add(Criteria.where("status").is(status));
        }
        if (category != null) {
            criteria.add(Criteria.where("category").is(category));
        }
        if (guidelineId != null && !guidelineId.isBlank()) {
            criteria.add(Criteria.where("guidelineId").is(guidelineId.trim()));
        }
        if (q != null && !q.isBlank()) {
            String regex = Pattern.quote(q.trim());
            criteria.add(new Criteria().orOperator(
                    Criteria.where("title").regex(regex, "i"),
                    Criteria.where("description").regex(regex, "i")
            ));
        }
        if (from != null || to != null) {
            Criteria createdAt = Criteria.where("createdAt");
            if (from != null) {
                createdAt.gte(from);
            }
            if (to != null) {
                createdAt.lte(to);
            }
            criteria.add(createdAt);
        }
        if (criteria.size() == 1) {
            query.addCriteria(criteria.get(0));
        } else if (criteria.size() > 1) {
            query.addCriteria(new Criteria().andOperator(criteria));
        }

        Page<IdeaDocument> result = MongoPages.find(mongoTemplate, query, pageable, IdeaDocument.class);
        return ApiPage.of(toResponses(result.getContent()), result);
    }

    public IdeaResponse getById(CurrentUser user, String id) {
        IdeaDocument idea = requireVisible(user, id);
        return toResponse(idea);
    }

    public IdeaResponse create(CurrentUser author, CreateIdeaRequest request) {
        IdeaDocument idea = new IdeaDocument();
        idea.setId(UUID.randomUUID().toString());
        idea.setTitle(request.title());
        idea.setDescription(request.description());
        idea.setCategory(request.category());
        idea.setAuthorId(author.id());
        idea.setStatus(IdeaStatus.PENDING);
        idea.setGuidelineId(resolveGuidelineId(request.guidelineId()));
        idea.setDecisionHistory(new ArrayList<>(List.of(historyItem(IdeaStatus.PENDING, author.id(), null))));
        // O id já vem preenchido, então o auditing trata o insert como update e não grava @CreatedDate.
        idea.setCreatedAt(Instant.now(clock));
        return toResponse(ideaRepository.save(idea));
    }

    public IdeaResponse update(CurrentUser actor, String id, UpdateIdeaRequest request) {
        IdeaDocument idea = requireMutable(actor, id);
        idea.setTitle(request.title());
        idea.setDescription(request.description());
        idea.setCategory(request.category());
        idea.setGuidelineId(resolveGuidelineId(request.guidelineId()));
        return toResponse(ideaRepository.save(idea));
    }

    public void delete(CurrentUser actor, String id) {
        IdeaDocument idea = requireMutable(actor, id);
        ideaRepository.delete(idea);
    }

    public IdeaResponse changeStatus(CurrentUser actor, String id, UpdateIdeaStatusRequest request) {
        IdeaDocument idea = ideaRepository.findById(id).orElseThrow(NotFoundException::new);
        IdeaLifecycle.denial(idea.getStatus(), request.status(), ideaHasProject(idea.getId()))
                .ifPresent(this::throwDenial);
        String justification = normalizeJustification(request.status(), request.justification());
        idea.setStatus(request.status());
        appendHistory(idea, historyItem(request.status(), actor.id(), justification));
        return toResponse(ideaRepository.save(idea));
    }

    private boolean ideaHasProject(String ideaId) {
        return projectRepository.existsByIdeaId(ideaId);
    }

    private void applyAuthorScope(CurrentUser user, String authorId, Boolean mine, List<Criteria> criteria) {
        if (user.role() == UserRole.OPERATOR) {
            criteria.add(Criteria.where("authorId").is(user.id()));
            return;
        }
        if (user.role() == UserRole.MANAGER && Boolean.TRUE.equals(mine)) {
            criteria.add(Criteria.where("authorId").is(user.id()));
            return;
        }
        if (authorId != null && !authorId.isBlank()) {
            criteria.add(Criteria.where("authorId").is(authorId.trim()));
        }
    }

    private IdeaDocument requireVisible(CurrentUser user, String id) {
        IdeaDocument idea = ideaRepository.findById(id).orElseThrow(NotFoundException::new);
        if (user.role() == UserRole.OPERATOR && !user.id().equals(idea.getAuthorId())) {
            throw new NotFoundException();
        }
        return idea;
    }

    private IdeaDocument requireMutable(CurrentUser actor, String id) {
        IdeaDocument idea = requireVisible(actor, id);
        if (ideaHasProject(idea.getId())) {
            throw ApiException.ideaAlreadyLinkedToProject();
        }
        if (idea.getStatus() != IdeaStatus.PENDING) {
            throw new BusinessException("Só é possível alterar ou excluir uma ideia pendente.");
        }
        return idea;
    }

    private String resolveGuidelineId(String guidelineId) {
        if (guidelineId == null) {
            return currentGuidelineId();
        }
        if (!guidelineRepository.existsById(guidelineId)) {
            throw new BusinessException("Diretriz informada não existe.");
        }
        return guidelineId;
    }

    private String currentGuidelineId() {
        Query query = new Query()
                .with(Sort.by(Sort.Direction.DESC, "updatedAt"))
                .limit(1);
        GuidelineDocument current = mongoTemplate.findOne(query, GuidelineDocument.class);
        return current == null ? null : current.getId();
    }

    private String normalizeJustification(IdeaStatus target, String raw) {
        String value = raw == null ? null : raw.trim();
        if (value != null && value.isEmpty()) {
            value = null;
        }
        if (target == IdeaStatus.REJECTED && (value == null || value.length() < 10)) {
            throw ApiException.validation(
                    "Um ou mais campos são inválidos.",
                    List.of(new ApiErrorDetail("justification", "deve ter no mínimo 10 caracteres"))
            );
        }
        if (value != null && value.length() > 1000) {
            throw ApiException.validation(
                    "Um ou mais campos são inválidos.",
                    List.of(new ApiErrorDetail("justification", "deve ter no máximo 1000 caracteres"))
            );
        }
        return value;
    }

    private DecisionHistoryItem historyItem(IdeaStatus status, String actorUserId, String justification) {
        DecisionHistoryItem item = new DecisionHistoryItem();
        item.setStatus(status);
        item.setActorUserId(actorUserId);
        item.setJustification(justification);
        item.setOccurredAt(Instant.now(clock));
        return item;
    }

    private void appendHistory(IdeaDocument idea, DecisionHistoryItem item) {
        List<DecisionHistoryItem> history = new ArrayList<>();
        if (idea.getDecisionHistory() != null) {
            history.addAll(idea.getDecisionHistory());
        }
        history.add(item);
        idea.setDecisionHistory(history);
    }

    private void throwDenial(IdeaLifecycle.Denial denial) {
        if (denial == IdeaLifecycle.Denial.IDEA_ALREADY_HAS_PROJECT) {
            throw ApiException.ideaAlreadyHasProject();
        }
        throw ApiException.invalidStatusTransition();
    }

    private IdeaResponse toResponse(IdeaDocument idea) {
        return toResponses(List.of(idea)).get(0);
    }

    private List<IdeaResponse> toResponses(List<IdeaDocument> ideas) {
        Map<String, String> names = namesById(actorIds(ideas));
        Map<String, String> titles = guidelineTitles(ideas);
        return ideas.stream().map(idea -> toResponse(idea, names, titles)).toList();
    }

    private IdeaResponse toResponse(IdeaDocument idea, Map<String, String> names, Map<String, String> titles) {
        List<DecisionHistoryItem> history = idea.getDecisionHistory() == null ? List.of() : idea.getDecisionHistory();
        List<DecisionHistoryResponse> decisionHistory = history.stream()
                .map(item -> DecisionHistoryResponse.from(item, names.get(item.getActorUserId())))
                .toList();
        return new IdeaResponse(
                idea.getId(),
                idea.getTitle(),
                idea.getDescription(),
                idea.getCategory(),
                idea.getAuthorId(),
                names.get(idea.getAuthorId()),
                idea.getStatus(),
                idea.getGuidelineId(),
                guidelineTitle(idea.getGuidelineId(), titles),
                decisionHistory,
                idea.getCreatedAt(),
                idea.getUpdatedAt()
        );
    }

    private String guidelineTitle(String guidelineId, Map<String, String> titles) {
        if (guidelineId == null) {
            return null;
        }
        return titles.getOrDefault(guidelineId, REMOVED_GUIDELINE_TITLE);
    }

    private List<String> actorIds(List<IdeaDocument> ideas) {
        List<String> ids = new ArrayList<>();
        for (IdeaDocument idea : ideas) {
            ids.add(idea.getAuthorId());
            if (idea.getDecisionHistory() == null) {
                continue;
            }
            for (DecisionHistoryItem item : idea.getDecisionHistory()) {
                ids.add(item.getActorUserId());
            }
        }
        return ids;
    }

    private Map<String, String> namesById(List<String> ids) {
        List<String> distinct = ids.stream().filter(id -> id != null && !id.isBlank()).distinct().toList();
        if (distinct.isEmpty()) {
            return Map.of();
        }
        return StreamSupport.stream(userRepository.findAllById(distinct).spliterator(), false)
                .collect(Collectors.toMap(UserDocument::getId, UserDocument::getName, (left, right) -> left));
    }

    private Map<String, String> guidelineTitles(List<IdeaDocument> ideas) {
        List<String> ids = ideas.stream()
                .map(IdeaDocument::getGuidelineId)
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
