package br.com.fiap.challengeaguiabranca.domain.model

/**
 * Agregação para o dashboard do Líder (calculada a partir dos projetos locais).
 */
data class RoiDashboardSummary(
    val totalInvestment: Double,
    val totalObtainedProfit: Double,
    val overallRoiPercent: Double,
    val averageProductivityGainPercent: Double,
    val activeProjectsCount: Int,
    val completedProjectsCount: Int
) {
    companion object {
        fun fromProjects(projects: List<Project>): RoiDashboardSummary {
            if (projects.isEmpty()) {
                return RoiDashboardSummary(
                    totalInvestment = 0.0,
                    totalObtainedProfit = 0.0,
                    overallRoiPercent = 0.0,
                    averageProductivityGainPercent = 0.0,
                    activeProjectsCount = 0,
                    completedProjectsCount = 0
                )
            }
            val investment = projects.sumOf { it.investmentAmount }
            val profit = projects.sumOf { it.obtainedProfit }
            val roi = if (investment > 0.0) ((profit - investment) / investment) * 100.0 else 0.0
            val avgProductivity = projects.map { it.productivityGainPercent }.average()
            return RoiDashboardSummary(
                totalInvestment = investment,
                totalObtainedProfit = profit,
                overallRoiPercent = roi,
                averageProductivityGainPercent = avgProductivity,
                activeProjectsCount = projects.count { it.status != ProjectStatus.COMPLETED },
                completedProjectsCount = projects.count { it.status == ProjectStatus.COMPLETED }
            )
        }
    }
}
