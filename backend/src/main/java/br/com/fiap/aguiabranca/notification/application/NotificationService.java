package br.com.fiap.aguiabranca.notification.application;

import java.time.Clock;
import java.time.Instant;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import br.com.fiap.aguiabranca.guideline.infra.GuidelineDocument;
import br.com.fiap.aguiabranca.idea.infra.IdeaDocument;
import br.com.fiap.aguiabranca.notification.api.dto.NotificationItemResponse;
import br.com.fiap.aguiabranca.notification.api.dto.NotificationsResponse;
import br.com.fiap.aguiabranca.security.current.CurrentUser;
import br.com.fiap.aguiabranca.suggestion.infra.SuggestionDocument;
import br.com.fiap.aguiabranca.user.domain.UserRole;
import br.com.fiap.aguiabranca.user.infra.UserDocument;
import br.com.fiap.aguiabranca.user.infra.UserRepository;

@Service
public class NotificationService {

    private final MongoTemplate mongoTemplate;
    private final UserRepository userRepository;
    private final Clock clock;

    public NotificationService(MongoTemplate mongoTemplate, UserRepository userRepository, Clock clock) {
        this.mongoTemplate = mongoTemplate;
        this.userRepository = userRepository;
        this.clock = clock;
    }

    public NotificationsResponse list(CurrentUser user) {
        Instant now = Instant.now(clock);
        List<NotificationItemResponse> items = switch (user.role()) {
            case OPERATOR -> NotificationProjector.forOperator(
                    allGuidelines(),
                    suggestions(Criteria.where("targetUserId").is(user.id())),
                    mongoTemplate.find(Query.query(Criteria.where("authorId").is(user.id())), IdeaDocument.class)
            );
            case MANAGER -> manager(user, now);
            case LEADER -> leader(now);
        };
        return new NotificationsResponse(items, !items.isEmpty());
    }

    private List<NotificationItemResponse> manager(CurrentUser user, Instant now) {
        List<IdeaDocument> ideas = mongoTemplate.findAll(IdeaDocument.class);
        List<SuggestionDocument> sent = suggestions(Criteria.where("authorUserId").is(user.id()));
        UserDirectory directory = directory(ideas);
        return NotificationProjector.forManager(ideas, sent, directory.names(), directory.operatorIds(), now);
    }

    private List<NotificationItemResponse> leader(Instant now) {
        List<IdeaDocument> ideas = mongoTemplate.findAll(IdeaDocument.class);
        UserDirectory directory = directory(ideas);
        return NotificationProjector.forLeader(ideas, allGuidelines(), directory.names(), now);
    }

    private List<GuidelineDocument> allGuidelines() {
        return mongoTemplate.find(new Query(), GuidelineDocument.class);
    }

    private List<SuggestionDocument> suggestions(Criteria criteria) {
        Query query = Query.query(criteria).with(Sort.by(Sort.Direction.DESC, "createdAt"));
        return mongoTemplate.find(query, SuggestionDocument.class);
    }

    private UserDirectory directory(List<IdeaDocument> ideas) {
        List<String> authorIds = ideas.stream().map(IdeaDocument::getAuthorId).distinct().toList();
        Map<String, String> names = new HashMap<>();
        Set<String> operatorIds = new HashSet<>();
        if (authorIds.isEmpty()) {
            return new UserDirectory(names, operatorIds);
        }
        for (UserDocument user : userRepository.findAllById(authorIds)) {
            names.put(user.getId(), user.getName());
            if (user.getRole() == UserRole.OPERATOR) {
                operatorIds.add(user.getId());
            }
        }
        return new UserDirectory(names, operatorIds);
    }

    private record UserDirectory(Map<String, String> names, Set<String> operatorIds) {
    }
}
