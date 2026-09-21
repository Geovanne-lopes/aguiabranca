package br.com.fiap.aguiabranca.dashboard.api.dto;

public record OperatorDashboardResponse(
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
