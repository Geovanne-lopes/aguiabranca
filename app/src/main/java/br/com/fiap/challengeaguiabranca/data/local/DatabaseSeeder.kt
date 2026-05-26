package br.com.fiap.challengeaguiabranca.data.local

import br.com.fiap.challengeaguiabranca.data.local.entity.IdeaEntity
import br.com.fiap.challengeaguiabranca.data.local.entity.StrategicGuidelineEntity
import br.com.fiap.challengeaguiabranca.domain.catalog.MockOperatorsCatalog
import br.com.fiap.challengeaguiabranca.domain.model.IdeaCategory
import br.com.fiap.challengeaguiabranca.domain.model.IdeaStatus
import br.com.fiap.challengeaguiabranca.domain.util.IdGenerator
import kotlinx.coroutines.flow.first

/**
 * Popula dados de demonstração quando o banco está vazio (apenas DEBUG).
 */
class DatabaseSeeder(
    private val database: InnovationDatabase
) {

    suspend fun seedIfEmpty(leaderAuthorId: String = "seed-leader") {
        seedGuidelinesIfEmpty(leaderAuthorId)
        seedMockIdeasIfEmpty()
    }

    private suspend fun seedGuidelinesIfEmpty(leaderAuthorId: String) {
        val guidelineDao = database.strategicGuidelineDao()
        if (guidelineDao.observeAll().first().isNotEmpty()) return

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

    suspend fun seedMockIdeasIfEmpty() {
        val ideaDao = database.ideaDao()
        if (ideaDao.observeAll().first().isNotEmpty()) return

        val now = System.currentTimeMillis()
        val day = 86_400_000L
        val mockIdeas = listOf(
            MockIdeaSeed("mock-op-joao", "App mobile para fila express", IdeaCategory.TECHNOLOGY, IdeaStatus.PENDING, 2),
            MockIdeaSeed("mock-op-maria", "Treinamento gamificado de vendas", IdeaCategory.PROCESS, IdeaStatus.PRIORITIZED, 4),
            MockIdeaSeed("mock-op-pedro", "Etiquetas digitais no estoque", IdeaCategory.TECHNOLOGY, IdeaStatus.APPROVED, 6),
            MockIdeaSeed("mock-op-lucia", "Coleta seletiva nas lojas", IdeaCategory.SUSTAINABILITY, IdeaStatus.PENDING, 1),
            MockIdeaSeed("mock-op-rafael", "Painel de metas por equipe", IdeaCategory.PROCESS, IdeaStatus.APPROVED, 8),
            MockIdeaSeed("mock-op-joao", "Reduzir fila no caixa rápido", IdeaCategory.OTHER, IdeaStatus.PENDING, 3),
            MockIdeaSeed("mock-op-maria", "Kit de boas-vindas digital", IdeaCategory.PRODUCT, IdeaStatus.APPROVED, 5),
            MockIdeaSeed("mock-op-pedro", "Alerta de validade de produtos", IdeaCategory.PROCESS, IdeaStatus.PENDING, 7),
            MockIdeaSeed("mock-op-lucia", "Horário flexível operacional", IdeaCategory.OTHER, IdeaStatus.REJECTED, 9),
            MockIdeaSeed("mock-op-rafael", "Dashboard de vendas por loja", IdeaCategory.TECHNOLOGY, IdeaStatus.PENDING, 10),
            MockIdeaSeed("mock-op-joao", "Wi-Fi exclusivo para clientes", IdeaCategory.TECHNOLOGY, IdeaStatus.PRIORITIZED, 11),
            MockIdeaSeed("mock-op-lucia", "Mural digital de reconhecimento", IdeaCategory.PROCESS, IdeaStatus.APPROVED, 12),
            MockIdeaSeed("mock-op-pedro", "Embalagens 100% recicláveis", IdeaCategory.SUSTAINABILITY, IdeaStatus.PENDING, 1),
            MockIdeaSeed("mock-op-rafael", "Caixa rápido com tablet itinerante", IdeaCategory.TECHNOLOGY, IdeaStatus.PENDING, 1),
            MockIdeaSeed("mock-op-joao", "Treinamento empático trimestral", IdeaCategory.PROCESS, IdeaStatus.APPROVED, 14),
            MockIdeaSeed("mock-op-maria", "Reciclagem de uniformes antigos", IdeaCategory.SUSTAINABILITY, IdeaStatus.PENDING, 2)
        )

        mockIdeas.forEach { seed ->
            ideaDao.insert(
                IdeaEntity(
                    id = IdGenerator.newId(),
                    title = seed.title,
                    description = "Ideia de demonstração enviada por ${MockOperatorsCatalog.resolveName(seed.authorId)} para curadoria do gestor.",
                    category = seed.category,
                    authorId = seed.authorId,
                    status = seed.status,
                    createdAtEpochMillis = now - day * seed.daysAgo
                )
            )
        }
    }

    private data class MockIdeaSeed(
        val authorId: String,
        val title: String,
        val category: IdeaCategory,
        val status: IdeaStatus,
        val daysAgo: Long
    )
}
