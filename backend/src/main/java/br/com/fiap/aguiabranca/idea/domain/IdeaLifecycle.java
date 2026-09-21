package br.com.fiap.aguiabranca.idea.domain;

import java.util.Optional;

/**
 * Máquina de status da ideia. O servidor é a autoridade.
 * {@code hasProject} é o gancho da fatia de projetos: reprovar a partir de
 * APPROVED ou PRIORITIZED é proibido quando já existe projeto.
 */
public final class IdeaLifecycle {

    public enum Denial {
        INVALID_STATUS_TRANSITION,
        IDEA_ALREADY_HAS_PROJECT
    }

    private IdeaLifecycle() {
    }

    public static Optional<Denial> denial(IdeaStatus from, IdeaStatus to, boolean hasProject) {
        if (!isAllowed(from, to)) {
            return Optional.of(Denial.INVALID_STATUS_TRANSITION);
        }
        if (hasProject && to == IdeaStatus.REJECTED && (from == IdeaStatus.APPROVED || from == IdeaStatus.PRIORITIZED)) {
            return Optional.of(Denial.IDEA_ALREADY_HAS_PROJECT);
        }
        return Optional.empty();
    }

    private static boolean isAllowed(IdeaStatus from, IdeaStatus to) {
        return switch (from) {
            case PENDING -> to == IdeaStatus.APPROVED || to == IdeaStatus.REJECTED || to == IdeaStatus.PRIORITIZED;
            case APPROVED -> to == IdeaStatus.PRIORITIZED || to == IdeaStatus.REJECTED;
            case PRIORITIZED -> to == IdeaStatus.REJECTED;
            case REJECTED -> false;
        };
    }
}
