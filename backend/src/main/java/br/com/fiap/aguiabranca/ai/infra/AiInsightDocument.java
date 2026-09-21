package br.com.fiap.aguiabranca.ai.infra;

import java.time.Instant;

import org.bson.Document;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

import br.com.fiap.aguiabranca.ai.domain.InsightRecordStatus;

@org.springframework.data.mongodb.core.mapping.Document("ai_insights")
public class AiInsightDocument {

    @Id
    private String id;
    private String requestedByUserId;
    private String model;
    private String promptHash;
    private int promptVersion;
    private Document inputSummary;
    private String content;
    private InsightRecordStatus status;
    private Instant createdAt;
    @Field("basisTotalProjects")
    private int basisTotalProjects;
    @Field("basisOverallRoiPercent")
    private double basisOverallRoiPercent;
    @Field("basisTotalInvestment")
    private double basisTotalInvestment;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRequestedByUserId() {
        return requestedByUserId;
    }

    public void setRequestedByUserId(String requestedByUserId) {
        this.requestedByUserId = requestedByUserId;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getPromptHash() {
        return promptHash;
    }

    public void setPromptHash(String promptHash) {
        this.promptHash = promptHash;
    }

    public int getPromptVersion() {
        return promptVersion;
    }

    public void setPromptVersion(int promptVersion) {
        this.promptVersion = promptVersion;
    }

    public Document getInputSummary() {
        return inputSummary;
    }

    public void setInputSummary(Document inputSummary) {
        this.inputSummary = inputSummary;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public InsightRecordStatus getStatus() {
        return status;
    }

    public void setStatus(InsightRecordStatus status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public int getBasisTotalProjects() {
        return basisTotalProjects;
    }

    public void setBasisTotalProjects(int basisTotalProjects) {
        this.basisTotalProjects = basisTotalProjects;
    }

    public double getBasisOverallRoiPercent() {
        return basisOverallRoiPercent;
    }

    public void setBasisOverallRoiPercent(double basisOverallRoiPercent) {
        this.basisOverallRoiPercent = basisOverallRoiPercent;
    }

    public double getBasisTotalInvestment() {
        return basisTotalInvestment;
    }

    public void setBasisTotalInvestment(double basisTotalInvestment) {
        this.basisTotalInvestment = basisTotalInvestment;
    }
}
