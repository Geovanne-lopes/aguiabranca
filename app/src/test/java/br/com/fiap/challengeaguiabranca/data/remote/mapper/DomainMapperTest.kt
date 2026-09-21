package br.com.fiap.challengeaguiabranca.data.remote.mapper

import br.com.fiap.challengeaguiabranca.data.remote.dto.IdeaResponseDto
import br.com.fiap.challengeaguiabranca.data.remote.dto.LeaderDashboardDto
import br.com.fiap.challengeaguiabranca.data.remote.dto.ProjectResponseDto
import br.com.fiap.challengeaguiabranca.data.remote.dto.SuggestionResponseDto
import br.com.fiap.challengeaguiabranca.domain.model.IdeaStatus
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.Instant

class DomainMapperTest {

    @Test
    fun mapsIdeaDtoToDomain() {
        val idea = IdeaResponseDto(
            id = "idea-1",
            title = "Coleta seletiva",
            description = "Pontos de coleta nas lojas da rede.",
            category = "SUSTAINABILITY",
            authorId = "user-1",
            authorName = "Marina",
            status = "PENDING",
            guidelineId = "gl-1",
            createdAt = "2026-09-21T16:00:00Z"
        ).toDomain()

        assertEquals("idea-1", idea.id)
        assertEquals("Marina", idea.authorName)
        assertEquals("gl-1", idea.guidelineId)
        assertEquals(IdeaStatus.PENDING, idea.status)
        assertEquals(Instant.parse("2026-09-21T16:00:00Z").toEpochMilli(), idea.createdAtEpochMillis)
    }

    @Test
    fun mapsProjectRoiAndDeadlineFromServer() {
        val project = ProjectResponseDto(
            id = "proj-1",
            ideaId = "idea-1",
            title = "Coleta seletiva",
            description = "Pontos de coleta nas lojas da rede.",
            status = "IN_DEVELOPMENT",
            investmentAmount = 200.0,
            obtainedProfit = 100.0,
            roiPercent = -50.0,
            deadline = "2026-12-31T00:00:00Z",
            managerId = "manager-1"
        ).toDomain()

        assertEquals(-50.0, project.roiPercent, 0.001)
        assertEquals(Instant.parse("2026-12-31T00:00:00Z").toEpochMilli(), project.deadlineEpochMillis)
    }

    @Test
    fun mapsLeaderDashboardWithoutRecalculatingRoi() {
        val dashboard = LeaderDashboardDto(
            totalInvestment = 200.0,
            totalObtainedProfit = 100.0,
            overallRoiPercent = -50.0,
            activeProjectsCount = 1,
            completedProjectsCount = 0
        ).toDomain()

        assertEquals(-50.0, dashboard.summary.overallRoiPercent, 0.001)
        assertEquals(200.0, dashboard.summary.totalInvestment, 0.001)
        assertEquals(100.0, dashboard.summary.totalObtainedProfit, 0.001)
    }

    @Test
    fun mapsSuggestionAuthorNameToManagerName() {
        val suggestion = SuggestionResponseDto(
            id = "sug-1",
            authorName = "Gestor",
            targetUserId = "op-1",
            targetEmail = "operador@innovatecorp.com",
            targetName = "Marina",
            message = "Priorize a diretriz vigente neste ciclo.",
            createdAt = "2026-09-21T16:00:00Z"
        ).toDomain()

        assertEquals("Gestor", suggestion.managerName)
        assertEquals("op-1", suggestion.targetAuthorId)
    }
}
