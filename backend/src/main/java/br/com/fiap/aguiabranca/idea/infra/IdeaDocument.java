package br.com.fiap.aguiabranca.idea.infra;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import br.com.fiap.aguiabranca.idea.domain.IdeaCategory;
import br.com.fiap.aguiabranca.idea.domain.IdeaStatus;

@Document("ideas")
public class IdeaDocument {

    @Id
    private String id;
    private String title;
    private String description;
    private IdeaCategory category;
    private String authorId;
    private IdeaStatus status = IdeaStatus.PENDING;
    private String guidelineId;
    private List<DecisionHistoryItem> decisionHistory = new ArrayList<>();
    @CreatedDate
    private Instant createdAt;
    @LastModifiedDate
    private Instant updatedAt;

    public IdeaDocument() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public IdeaCategory getCategory() {
        return category;
    }

    public void setCategory(IdeaCategory category) {
        this.category = category;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public IdeaStatus getStatus() {
        return status;
    }

    public void setStatus(IdeaStatus status) {
        this.status = status;
    }

    public String getGuidelineId() {
        return guidelineId;
    }

    public void setGuidelineId(String guidelineId) {
        this.guidelineId = guidelineId;
    }

    public List<DecisionHistoryItem> getDecisionHistory() {
        return decisionHistory;
    }

    public void setDecisionHistory(List<DecisionHistoryItem> decisionHistory) {
        this.decisionHistory = decisionHistory;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
