package br.com.fiap.aguiabranca.suggestion.application;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import br.com.fiap.aguiabranca.common.api.ApiPage;
import br.com.fiap.aguiabranca.common.api.Pagination;
import br.com.fiap.aguiabranca.common.exception.BusinessException;
import br.com.fiap.aguiabranca.common.exception.NotFoundException;
import br.com.fiap.aguiabranca.common.persistence.MongoPages;
import br.com.fiap.aguiabranca.security.current.CurrentUser;
import br.com.fiap.aguiabranca.suggestion.api.dto.CreateSuggestionRequest;
import br.com.fiap.aguiabranca.suggestion.api.dto.SuggestionResponse;
import br.com.fiap.aguiabranca.suggestion.infra.SuggestionDocument;
import br.com.fiap.aguiabranca.suggestion.infra.SuggestionRepository;
import br.com.fiap.aguiabranca.user.domain.UserRole;
import br.com.fiap.aguiabranca.user.infra.UserDocument;
import br.com.fiap.aguiabranca.user.infra.UserRepository;

@Service
public class SuggestionService {

    static final Set<String> SORT_FIELDS = Set.of("createdAt");

    private final SuggestionRepository suggestionRepository;
    private final UserRepository userRepository;
    private final MongoTemplate mongoTemplate;
    private final Clock clock;

    public SuggestionService(
            SuggestionRepository suggestionRepository,
            UserRepository userRepository,
            MongoTemplate mongoTemplate,
            Clock clock
    ) {
        this.suggestionRepository = suggestionRepository;
        this.userRepository = userRepository;
        this.mongoTemplate = mongoTemplate;
        this.clock = clock;
    }

    public SuggestionResponse create(CurrentUser author, CreateSuggestionRequest request) {
        UserDocument authorUser = userRepository.findById(author.id()).orElseThrow(NotFoundException::new);
        UserDocument target = userRepository.findById(request.targetUserId()).orElseThrow(NotFoundException::new);
        if (target.getRole() != UserRole.OPERATOR || !target.isActive()) {
            throw new BusinessException("O destinatário deve ser um operador ativo.");
        }

        SuggestionDocument document = new SuggestionDocument();
        document.setId(UUID.randomUUID().toString());
        document.setAuthorUserId(authorUser.getId());
        document.setAuthorName(authorUser.getName());
        document.setTargetUserId(target.getId());
        document.setTargetEmail(target.getEmail());
        document.setTargetName(target.getName());
        document.setMessage(request.message());
        document.setCreatedAt(Instant.now(clock));
        return SuggestionResponse.from(suggestionRepository.save(document));
    }

    public ApiPage<SuggestionResponse> list(CurrentUser user, Integer page, Integer size, String sort, String direction) {
        PageRequest pageable = Pagination.of(page, size, sort, direction, SORT_FIELDS, "createdAt");
        String scopeField = user.role() == UserRole.OPERATOR ? "targetUserId" : "authorUserId";
        Query query = new Query(Criteria.where(scopeField).is(user.id()));
        Page<SuggestionDocument> result = MongoPages.find(mongoTemplate, query, pageable, SuggestionDocument.class);
        List<SuggestionResponse> content = result.getContent().stream().map(SuggestionResponse::from).toList();
        return ApiPage.of(content, result);
    }
}
