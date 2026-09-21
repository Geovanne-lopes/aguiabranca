package br.com.fiap.aguiabranca.dashboard.api.dto;

import java.util.List;

public record ManagerDashboardResponse(
        int pendingIdeasCount,
        int activeProjectsCount,
        int ideasReceivedThisMonth,
        int ideasReceivedPreviousMonth,
        String ideasReceivedTrendLabel,
        int approvalRatePercent,
        String approvalRateTrendLabel,
        List<MonthBarResponse> monthlyBars,
        List<StatusCountResponse> ideasByStatus,
        boolean hasData
) {
}
