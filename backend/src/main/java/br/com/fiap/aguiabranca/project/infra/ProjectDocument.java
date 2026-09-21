package br.com.fiap.aguiabranca.project.infra;

import java.time.Instant;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import br.com.fiap.aguiabranca.project.domain.ProjectStatus;

@Document("projects")
public class ProjectDocument {

    @Id
    private String id;
    private String ideaId;
    private String guidelineId;
    private String title;
    private String description;
    private ProjectStatus status = ProjectStatus.BACKLOG;
    private double investmentAmount;
    private double obtainedProfit;
    private double productivityGainPercent;
    private Instant deadline;
    private String managerId;
    @CreatedDate
    private Instant createdAt;
    @LastModifiedDate
    private Instant updatedAt;

    public ProjectDocument() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdeaId() {
        return ideaId;
    }

    public void setIdeaId(String ideaId) {
        this.ideaId = ideaId;
    }

    public String getGuidelineId() {
        return guidelineId;
    }

    public void setGuidelineId(String guidelineId) {
        this.guidelineId = guidelineId;
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

    public ProjectStatus getStatus() {
        return status;
    }

    public void setStatus(ProjectStatus status) {
        this.status = status;
    }

    public double getInvestmentAmount() {
        return investmentAmount;
    }

    public void setInvestmentAmount(double investmentAmount) {
        this.investmentAmount = investmentAmount;
    }

    public double getObtainedProfit() {
        return obtainedProfit;
    }

    public void setObtainedProfit(double obtainedProfit) {
        this.obtainedProfit = obtainedProfit;
    }

    public double getProductivityGainPercent() {
        return productivityGainPercent;
    }

    public void setProductivityGainPercent(double productivityGainPercent) {
        this.productivityGainPercent = productivityGainPercent;
    }

    public Instant getDeadline() {
        return deadline;
    }

    public void setDeadline(Instant deadline) {
        this.deadline = deadline;
    }

    public String getManagerId() {
        return managerId;
    }

    public void setManagerId(String managerId) {
        this.managerId = managerId;
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
