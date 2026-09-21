package br.com.fiap.aguiabranca.config;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.index.TextIndexDefinition;
import org.springframework.stereotype.Component;

import br.com.fiap.aguiabranca.ai.infra.AiInsightDocument;
import br.com.fiap.aguiabranca.auth.infra.RefreshTokenDocument;
import br.com.fiap.aguiabranca.guideline.infra.GuidelineDocument;
import br.com.fiap.aguiabranca.guideline.infra.GuidelineHistoryDocument;
import br.com.fiap.aguiabranca.idea.infra.IdeaDocument;
import br.com.fiap.aguiabranca.project.infra.ProjectDocument;
import br.com.fiap.aguiabranca.suggestion.infra.SuggestionDocument;
import br.com.fiap.aguiabranca.user.infra.UserDocument;

@Component
@Order(1)
public class MongoIndexConfig implements ApplicationRunner {

    private final MongoTemplate mongoTemplate;

    public MongoIndexConfig(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void run(ApplicationArguments args) {
        mongoTemplate.indexOps(UserDocument.class)
                .ensureIndex(new Index().on("email", Sort.Direction.ASC).unique());
        mongoTemplate.indexOps(UserDocument.class)
                .ensureIndex(new Index().on("role", Sort.Direction.ASC));

        mongoTemplate.indexOps(RefreshTokenDocument.class)
                .ensureIndex(new Index().on("tokenHash", Sort.Direction.ASC).unique());
        mongoTemplate.indexOps(RefreshTokenDocument.class)
                .ensureIndex(new Index().on("userId", Sort.Direction.ASC));
        mongoTemplate.indexOps(RefreshTokenDocument.class)
                .ensureIndex(new Index().on("expiresAt", Sort.Direction.ASC).expire(0));

        mongoTemplate.indexOps(GuidelineDocument.class)
                .ensureIndex(new Index().on("updatedAt", Sort.Direction.DESC));

        mongoTemplate.indexOps(GuidelineHistoryDocument.class)
                .ensureIndex(new Index().on("guidelineId", Sort.Direction.ASC).on("version", Sort.Direction.ASC).unique());
        mongoTemplate.indexOps(GuidelineHistoryDocument.class)
                .ensureIndex(new Index().on("occurredAt", Sort.Direction.DESC));

        mongoTemplate.indexOps(IdeaDocument.class)
                .ensureIndex(new Index().on("authorId", Sort.Direction.ASC).on("createdAt", Sort.Direction.DESC));
        mongoTemplate.indexOps(IdeaDocument.class)
                .ensureIndex(new Index().on("status", Sort.Direction.ASC).on("createdAt", Sort.Direction.DESC));
        mongoTemplate.indexOps(IdeaDocument.class)
                .ensureIndex(new Index().on("guidelineId", Sort.Direction.ASC));
        mongoTemplate.indexOps(IdeaDocument.class)
                .ensureIndex(new TextIndexDefinition.TextIndexDefinitionBuilder()
                        .onField("title")
                        .onField("description")
                        .build());

        mongoTemplate.indexOps(ProjectDocument.class)
                .ensureIndex(new Index().on("ideaId", Sort.Direction.ASC).unique());
        mongoTemplate.indexOps(ProjectDocument.class)
                .ensureIndex(new Index().on("status", Sort.Direction.ASC).on("updatedAt", Sort.Direction.DESC));
        mongoTemplate.indexOps(ProjectDocument.class)
                .ensureIndex(new Index().on("guidelineId", Sort.Direction.ASC));
        mongoTemplate.indexOps(ProjectDocument.class)
                .ensureIndex(new Index().on("managerId", Sort.Direction.ASC));
        mongoTemplate.indexOps(ProjectDocument.class)
                .ensureIndex(new Index().on("updatedAt", Sort.Direction.DESC));

        mongoTemplate.indexOps(SuggestionDocument.class)
                .ensureIndex(new Index().on("targetUserId", Sort.Direction.ASC).on("createdAt", Sort.Direction.DESC));
        mongoTemplate.indexOps(SuggestionDocument.class)
                .ensureIndex(new Index().on("authorUserId", Sort.Direction.ASC).on("createdAt", Sort.Direction.DESC));

        mongoTemplate.indexOps(AiInsightDocument.class)
                .ensureIndex(new Index().on("requestedByUserId", Sort.Direction.ASC).on("createdAt", Sort.Direction.DESC));
    }
}
