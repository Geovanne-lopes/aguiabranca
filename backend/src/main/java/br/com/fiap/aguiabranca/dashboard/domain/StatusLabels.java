package br.com.fiap.aguiabranca.dashboard.domain;

import br.com.fiap.aguiabranca.idea.domain.IdeaStatus;
import br.com.fiap.aguiabranca.project.domain.ProjectStatus;

public final class StatusLabels {

    private StatusLabels() {
    }

    public static String idea(IdeaStatus status) {
        return switch (status) {
            case PENDING -> "Pendente";
            case APPROVED -> "Aprovada";
            case REJECTED -> "Reprovada";
            case PRIORITIZED -> "Priorizada";
        };
    }

    public static String project(ProjectStatus status) {
        return switch (status) {
            case BACKLOG -> "Planejamento";
            case IN_DEVELOPMENT -> "Em execução";
            case URGENT_DEADLINE -> "Prazo urgente";
            case AVERAGE_TICKET -> "Ticket médio";
            case COMPLETED -> "Concluído";
        };
    }
}
