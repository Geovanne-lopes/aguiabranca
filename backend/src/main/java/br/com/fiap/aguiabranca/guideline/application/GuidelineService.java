package br.com.fiap.aguiabranca.guideline.application;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import br.com.fiap.aguiabranca.common.api.ApiPage;
import br.com.fiap.aguiabranca.common.api.Pagination;
import br.com.fiap.aguiabranca.common.exception.NotFoundException;
import br.com.fiap.aguiabranca.common.persistence.MongoPages;
import br.com.fiap.aguiabranca.guideline.api.dto.GuidelineHistoryResponse;
import br.com.fiap.aguiabranca.guideline.api.dto.GuidelineResponse;
import br.com.fiap.aguiabranca.guideline.api.dto.GuidelineWriteRequest;
import br.com.fiap.aguiabranca.guideline.domain.GuidelineHistoryAction;
import br.com.fiap.aguiabranca.guideline.infra.GuidelineDocument;
import br.com.fiap.aguiabranca.guideline.infra.GuidelineHistoryDocument;
import br.com.fiap.aguiabranca.guideline.infra.GuidelineHistoryRepository;
import br.com.fiap.aguiabranca.guideline.infra.GuidelineRepository;
import br.com.fiap.aguiabranca.security.current.CurrentUser;
import br.com.fiap.aguiabranca.user.infra.UserDocument;
import br.com.fiap.aguiabranca.user.infra.UserRepository;

@Service
public class GuidelineService {

    static final Set<String> LIST_SORT_FIELDS = Set.of("updatedAt", "createdAt", "title", "version");
    static final Set<String> HISTORY_SORT_FIELDS = Set.of("version", "occurredAt");

    private final GuidelineRepository guidelineRepository;
    private final GuidelineHistoryRepository historyRepository;
    private final UserRepository userRepository;
    private final MongoTemplate mongoTemplate;
    private final Clock clock;

    public GuidelineService(
            GuidelineRepository guidelineRepository,
            GuidelineHistoryRepository historyRepository,
            UserRepository userRepository,
            MongoTemplate mongoTemplate,
            Clock clock
    ) {
        this.guidelineRepository = guidelineRepository;
        this.historyRepository = historyRepository;
        this.userRepository = userRepository;
        this.mongoTemplate = mongoTemplate;
        this.clock = clock;
    }

    public ApiPage<GuidelineResponse> list(Integer page, Integer size, String sort, String direction, String q) {
        PageRequest pageable = Pagination.of(page, size, sort, direction, LIST_SORT_FIELDS, "updatedAt");
        Query query = new Query();
        if (q != null && !q.isBlank()) {
            String regex = Pattern.quote(q.trim());
            query.addCriteria(new Criteria().orOperator(
                    Criteria.where("title").regex(regex, "i"),
                    Criteria.where("content").regex(regex, "i")
            ));
        }
        Page<GuidelineDocument> result = MongoPages.find(mongoTemplate, query, pageable, GuidelineDocument.class);
        Map<String, String> names = namesById(result.getContent().stream().map(GuidelineDocument::getAuthorId).toList());
        List<GuidelineResponse> content = result.getContent().stream()
                .map(doc -> GuidelineResponse.from(doc, names.get(doc.getAuthorId())))
                .toList();
        return ApiPage.of(content, result);
    }

    public GuidelineResponse getById(String id) {
        GuidelineDocument guideline = requireGuideline(id);
        return GuidelineResponse.from(guideline, nameOf(guideline.getAuthorId()));
    }

    public GuidelineResponse create(CurrentUser actor, GuidelineWriteRequest request) {
        GuidelineDocument guideline = new GuidelineDocument();
        guideline.setId(UUID.randomUUID().toString());
        guideline.setTitle(request.title().trim());
        guideline.setContent(request.content().trim());
        guideline.setCategory(optionalText(request.category()));
        guideline.setCampaign(optionalText(request.campaign()));
        guideline.setVersion(1);
        guideline.setAuthorId(actor.id());

        appendHistory(guideline, GuidelineHistoryAction.CREATED, actor.id());
        GuidelineDocument saved = guidelineRepository.save(guideline);
        return GuidelineResponse.from(saved, nameOf(saved.getAuthorId()));
    }

    public GuidelineResponse update(CurrentUser actor, String id, GuidelineWriteRequest request) {
        GuidelineDocument guideline = requireGuideline(id);
        guideline.setTitle(request.title().trim());
        guideline.setContent(request.content().trim());
        guideline.setCategory(optionalText(request.category()));
        guideline.setCampaign(optionalText(request.campaign()));
        guideline.setVersion(guideline.getVersion() + 1);

        appendHistory(guideline, GuidelineHistoryAction.UPDATED, actor.id());
        GuidelineDocument saved = guidelineRepository.save(guideline);
        return GuidelineResponse.from(saved, nameOf(saved.getAuthorId()));
    }

    public void delete(CurrentUser actor, String id) {
        GuidelineDocument guideline = requireGuideline(id);
        // ADR-004 exige unique (guidelineId, version). DELETED é um evento novo na linha do tempo.
        guideline.setVersion(guideline.getVersion() + 1);
        appendHistory(guideline, GuidelineHistoryAction.DELETED, actor.id());
        guidelineRepository.delete(guideline);
    }

    public ApiPage<GuidelineHistoryResponse> history(
            String guidelineId,
            Integer page,
            Integer size,
            String sort,
            String direction
    ) {
        PageRequest pageable = Pagination.of(page, size, sort, direction, HISTORY_SORT_FIELDS, "version");
        Page<GuidelineHistoryDocument> result = historyRepository.findByGuidelineId(guidelineId, pageable);
        Map<String, String> names = namesById(result.getContent().stream().map(GuidelineHistoryDocument::getActorUserId).toList());
        List<GuidelineHistoryResponse> content = result.getContent().stream()
                .map(doc -> GuidelineHistoryResponse.from(doc, names.get(doc.getActorUserId())))
                .toList();
        return ApiPage.of(content, result);
    }

    private GuidelineDocument requireGuideline(String id) {
        return guidelineRepository.findById(id).orElseThrow(NotFoundException::new);
    }

    private void appendHistory(GuidelineDocument guideline, GuidelineHistoryAction action, String actorUserId) {
        GuidelineHistoryDocument history = new GuidelineHistoryDocument();
        history.setId(UUID.randomUUID().toString());
        history.setGuidelineId(guideline.getId());
        history.setVersion(guideline.getVersion());
        history.setAction(action);
        history.setTitle(guideline.getTitle());
        history.setContent(guideline.getContent());
        history.setCategory(guideline.getCategory());
        history.setCampaign(guideline.getCampaign());
        history.setActorUserId(actorUserId);
        history.setOccurredAt(Instant.now(clock));
        historyRepository.save(history);
    }

    private String nameOf(String userId) {
        if (userId == null) {
            return null;
        }
        return userRepository.findById(userId).map(UserDocument::getName).orElse(null);
    }

    private Map<String, String> namesById(List<String> ids) {
        List<String> distinct = ids.stream().filter(id -> id != null && !id.isBlank()).distinct().toList();
        if (distinct.isEmpty()) {
            return Map.of();
        }
        return StreamSupport.stream(userRepository.findAllById(distinct).spliterator(), false)
                .collect(Collectors.toMap(UserDocument::getId, UserDocument::getName, (left, right) -> left));
    }

    private static String optionalText(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}
