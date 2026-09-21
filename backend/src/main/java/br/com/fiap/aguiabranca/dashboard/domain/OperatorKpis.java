package br.com.fiap.aguiabranca.dashboard.domain;

import java.time.Clock;
import java.time.Instant;
import java.util.List;

import br.com.fiap.aguiabranca.idea.domain.IdeaStatus;

/**
 * Contagens do operador. A tendência de aprovadas usa o {@code occurredAt} do primeiro
 * evento APPROVED ou PRIORITIZED, não o {@code createdAt} da ideia.
 */
public final class OperatorKpis {

    private OperatorKpis() {
    }

    public record IdeaPoint(IdeaStatus status, Instant createdAt, Instant firstApprovalAt) {
    }

    public record Result(
            int ideasSubmittedCount,
            int ideasApprovedCount,
            int ideasPrioritizedCount,
            int ideasRejectedCount,
            int ideasPendingCount,
            int submittedThisMonth,
            int submittedPreviousMonth,
            int submittedTrendPercent,
            String submittedTrendLabel,
            int approvedThisMonth,
            int approvedPreviousMonth,
            String approvedTrendLabel,
            boolean hasData
    ) {
    }

    public static Result compute(List<IdeaPoint> ideas, Clock clock) {
        int submitted = 0;
        int approved = 0;
        int prioritized = 0;
        int rejected = 0;
        int pending = 0;
        Instant currentStart = SaoPauloCalendar.startOfMonth(clock);
        Instant nextStart = SaoPauloCalendar.startOfNextMonth(clock);
        Instant previousStart = SaoPauloCalendar.startOfPreviousMonth(clock);
        int submittedCurrent = 0;
        int submittedPrevious = 0;
        int approvedCurrent = 0;
        int approvedPrevious = 0;
        for (IdeaPoint idea : ideas) {
            submitted++;
            if (idea.status() == IdeaStatus.APPROVED) {
                approved++;
            } else if (idea.status() == IdeaStatus.PRIORITIZED) {
                prioritized++;
            } else if (idea.status() == IdeaStatus.REJECTED) {
                rejected++;
            } else if (idea.status() == IdeaStatus.PENDING) {
                pending++;
            }
            if (SaoPauloCalendar.inMonth(idea.createdAt(), currentStart, nextStart)) {
                submittedCurrent++;
            } else if (SaoPauloCalendar.inMonth(idea.createdAt(), previousStart, currentStart)) {
                submittedPrevious++;
            }
            if (SaoPauloCalendar.inMonth(idea.firstApprovalAt(), currentStart, nextStart)) {
                approvedCurrent++;
            } else if (SaoPauloCalendar.inMonth(idea.firstApprovalAt(), previousStart, currentStart)) {
                approvedPrevious++;
            }
        }
        return new Result(
                submitted,
                approved,
                prioritized,
                rejected,
                pending,
                submittedCurrent,
                submittedPrevious,
                MonthTrend.percent(submittedCurrent, submittedPrevious),
                MonthTrend.label(submittedCurrent, submittedPrevious),
                approvedCurrent,
                approvedPrevious,
                MonthTrend.label(approvedCurrent, approvedPrevious),
                submitted > 0
        );
    }
}
