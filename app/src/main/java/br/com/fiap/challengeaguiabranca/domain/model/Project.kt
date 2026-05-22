package br.com.fiap.challengeaguiabranca.domain.model

/**
 * Projeto ativo derivado de uma ideia aprovada.
 * Campos financeiros alimentam o dashboard do Líder.
 */
data class Project(
    val id: String,
    val ideaId: String,
    val title: String,
    val description: String,
    val status: ProjectStatus = ProjectStatus.BACKLOG,
    val investmentAmount: Double = 0.0,
    val obtainedProfit: Double = 0.0,
    /** Ganho de produtividade em percentual (0–100) ou índice definido pelo gestor. */
    val productivityGainPercent: Double = 0.0,
    val deadlineEpochMillis: Long? = null,
    val managerId: String,
    val createdAtEpochMillis: Long = System.currentTimeMillis(),
    val updatedAtEpochMillis: Long = System.currentTimeMillis()
) {
    val roiPercent: Double
        get() = if (investmentAmount > 0.0) {
            ((obtainedProfit - investmentAmount) / investmentAmount) * 100.0
        } else {
            0.0
        }
}
