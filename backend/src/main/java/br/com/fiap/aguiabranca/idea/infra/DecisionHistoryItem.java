package br.com.fiap.aguiabranca.idea.infra;

import java.time.Instant;

import br.com.fiap.aguiabranca.idea.domain.IdeaStatus;

public class DecisionHistoryItem {

    private IdeaStatus status;
    private String actorUserId;
    private String justification;
    private Instant occurredAt;

    public DecisionHistoryItem() {
    }

    public IdeaStatus getStatus() {
        return status;
    }

    public void setStatus(IdeaStatus status) {
        this.status = status;
    }

    public String getActorUserId() {
        return actorUserId;
    }

    public void setActorUserId(String actorUserId) {
        this.actorUserId = actorUserId;
    }

    public String getJustification() {
        return justification;
    }

    public void setJustification(String justification) {
        this.justification = justification;
    }

    public Instant getOccurredAt() {
        return occurredAt;
    }

    public void setOccurredAt(Instant occurredAt) {
        this.occurredAt = occurredAt;
    }
}
