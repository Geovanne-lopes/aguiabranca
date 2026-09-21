package br.com.fiap.aguiabranca.user.application;

import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import br.com.fiap.aguiabranca.auth.application.AuthService;
import br.com.fiap.aguiabranca.common.api.ApiPage;
import br.com.fiap.aguiabranca.common.api.Pagination;
import br.com.fiap.aguiabranca.common.exception.ApiException;
import br.com.fiap.aguiabranca.common.persistence.MongoPages;
import br.com.fiap.aguiabranca.security.current.CurrentUser;
import br.com.fiap.aguiabranca.user.api.dto.UpdateProfileRequest;
import br.com.fiap.aguiabranca.user.api.dto.UserResponse;
import br.com.fiap.aguiabranca.user.domain.UserRole;
import br.com.fiap.aguiabranca.user.infra.UserDocument;
import br.com.fiap.aguiabranca.user.infra.UserRepository;

@Service
public class UserService {

    static final Set<String> LIST_SORT_FIELDS = Set.of("name", "email", "role", "createdAt", "updatedAt");

    private final UserRepository userRepository;
    private final AuthService authService;
    private final MongoTemplate mongoTemplate;

    public UserService(UserRepository userRepository, AuthService authService, MongoTemplate mongoTemplate) {
        this.userRepository = userRepository;
        this.authService = authService;
        this.mongoTemplate = mongoTemplate;
    }

    public ApiPage<UserResponse> list(
            UserRole role,
            String q,
            Integer page,
            Integer size,
            String sort,
            String direction
    ) {
        PageRequest pageable = Pagination.of(page, size, sort, direction, LIST_SORT_FIELDS, "createdAt");
        Query query = new Query();
        if (role != null) {
            query.addCriteria(Criteria.where("role").is(role));
        }
        if (q != null && !q.isBlank()) {
            String regex = Pattern.quote(q.trim());
            query.addCriteria(new Criteria().orOperator(
                    Criteria.where("name").regex(regex, "i"),
                    Criteria.where("email").regex(regex, "i")
            ));
        }
        Page<UserDocument> result = MongoPages.find(mongoTemplate, query, pageable, UserDocument.class);
        List<UserResponse> content = result.getContent().stream().map(UserResponse::from).toList();
        return ApiPage.of(content, result);
    }

    public UserResponse updateMe(CurrentUser currentUser, UpdateProfileRequest request) {
        authService.validateProfilePatch(request.name(), request.email(), request.avatarUrl());
        UserDocument user = authService.requireUser(currentUser.id());
        if (request.name() != null && !request.name().isBlank()) {
            user.setName(request.name().trim());
        }
        if (request.email() != null && !request.email().isBlank()) {
            String normalized = request.email().trim().toLowerCase();
            if (!normalized.equals(user.getEmail()) && userRepository.existsByEmail(normalized)) {
                throw ApiException.emailAlreadyExists();
            }
            user.setEmail(normalized);
        }
        if (request.avatarUrl() != null) {
            String avatar = request.avatarUrl().isBlank() ? null : request.avatarUrl().trim();
            user.setAvatarUrl(avatar);
        }
        return UserResponse.from(userRepository.save(user));
    }
}
