package br.com.fiap.challengeaguiabranca.data.local

import br.com.fiap.challengeaguiabranca.data.local.entity.StrategicGuidelineEntity
import br.com.fiap.challengeaguiabranca.domain.util.IdGenerator
import kotlinx.coroutines.flow.first

/**
 * Popula dados de demonstração quando o banco está vazio (apenas DEBUG).
 */
class DatabaseSeeder(
    private val database: InnovationDatabase
) {

    suspend fun seedIfEmpty(leaderAuthorId: String = "seed-leader") {
        val guidelineDao = database.strategicGuidelineDao()
        val existing = guidelineDao.observeAll().first()
        if (existing.isNotEmpty()) return

        val now = System.currentTimeMillis()
        val samples = listOf(
            StrategicGuidelineEntity(
                id = IdGenerator.newId(),
                title = "Transformação Digital",
                content = "Priorizar iniciativas que digitalizem processos operacionais e reduzam retrabalho.",
                authorId = leaderAuthorId,
                createdAtEpochMillis = now,
                updatedAtEpochMillis = now
            ),
            StrategicGuidelineEntity(
                id = IdGenerator.newId(),
                title = "Sustentabilidade Corporativa",
                content = "Incentivar ideias com impacto ambiental mensurável e metas ESG claras.",
                authorId = leaderAuthorId,
                createdAtEpochMillis = now,
                updatedAtEpochMillis = now
            )
        )
        samples.forEach { guidelineDao.insert(it) }
    }
}
