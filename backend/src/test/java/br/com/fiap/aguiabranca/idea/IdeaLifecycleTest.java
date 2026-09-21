package br.com.fiap.aguiabranca.idea;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import br.com.fiap.aguiabranca.idea.domain.IdeaLifecycle;
import br.com.fiap.aguiabranca.idea.domain.IdeaLifecycle.Denial;
import br.com.fiap.aguiabranca.idea.domain.IdeaStatus;

class IdeaLifecycleTest {

    @Test
    void pendingAllowsApprovedRejectedAndPrioritized() {
        assertThat(IdeaLifecycle.denial(IdeaStatus.PENDING, IdeaStatus.APPROVED, false)).isEmpty();
        assertThat(IdeaLifecycle.denial(IdeaStatus.PENDING, IdeaStatus.REJECTED, false)).isEmpty();
        assertThat(IdeaLifecycle.denial(IdeaStatus.PENDING, IdeaStatus.PRIORITIZED, false)).isEmpty();
        assertThat(IdeaLifecycle.denial(IdeaStatus.PENDING, IdeaStatus.PENDING, false)).contains(Denial.INVALID_STATUS_TRANSITION);
    }

    @Test
    void approvedAllowsPrioritizedAndRejectedUntilAProjectExists() {
        assertThat(IdeaLifecycle.denial(IdeaStatus.APPROVED, IdeaStatus.PRIORITIZED, false)).isEmpty();
        assertThat(IdeaLifecycle.denial(IdeaStatus.APPROVED, IdeaStatus.REJECTED, false)).isEmpty();
        assertThat(IdeaLifecycle.denial(IdeaStatus.APPROVED, IdeaStatus.REJECTED, true)).contains(Denial.IDEA_ALREADY_HAS_PROJECT);
        assertThat(IdeaLifecycle.denial(IdeaStatus.APPROVED, IdeaStatus.PENDING, false)).contains(Denial.INVALID_STATUS_TRANSITION);
        assertThat(IdeaLifecycle.denial(IdeaStatus.APPROVED, IdeaStatus.APPROVED, false)).contains(Denial.INVALID_STATUS_TRANSITION);
    }

    @Test
    void prioritizedCanOnlyBeRejectedAndNotWhenAProjectExists() {
        assertThat(IdeaLifecycle.denial(IdeaStatus.PRIORITIZED, IdeaStatus.REJECTED, false)).isEmpty();
        assertThat(IdeaLifecycle.denial(IdeaStatus.PRIORITIZED, IdeaStatus.REJECTED, true)).contains(Denial.IDEA_ALREADY_HAS_PROJECT);
        assertThat(IdeaLifecycle.denial(IdeaStatus.PRIORITIZED, IdeaStatus.APPROVED, false)).contains(Denial.INVALID_STATUS_TRANSITION);
        assertThat(IdeaLifecycle.denial(IdeaStatus.PRIORITIZED, IdeaStatus.PRIORITIZED, false)).contains(Denial.INVALID_STATUS_TRANSITION);
    }

    @Test
    void rejectedIsTerminal() {
        for (IdeaStatus target : IdeaStatus.values()) {
            assertThat(IdeaLifecycle.denial(IdeaStatus.REJECTED, target, false)).contains(Denial.INVALID_STATUS_TRANSITION);
        }
    }
}
