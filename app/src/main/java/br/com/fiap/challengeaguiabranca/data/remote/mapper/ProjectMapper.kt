package br.com.fiap.challengeaguiabranca.data.remote.mapper

import br.com.fiap.challengeaguiabranca.data.remote.dto.ProjectResponseDto
import br.com.fiap.challengeaguiabranca.data.remote.dto.UpdateProjectRequestDto
import br.com.fiap.challengeaguiabranca.domain.model.Project
import br.com.fiap.challengeaguiabranca.domain.model.ProjectStatus

fun ProjectResponseDto.toDomain(): Project = Project(
    id = id,
    ideaId = ideaId,
    title = title,
    description = description,
    status = status.toProjectStatus(),
    investmentAmount = investmentAmount,
    obtainedProfit = obtainedProfit,
    productivityGainPercent = productivityGainPercent,
    deadlineEpochMillis = deadline.toEpochMillisOrNull(),
    managerId = managerId,
    createdAtEpochMillis = createdAt.toEpochMillis(),
    updatedAtEpochMillis = updatedAt.toEpochMillis(),
    guidelineId = guidelineId,
    guidelineTitle = guidelineTitle,
    reportedRoiPercent = roiPercent
)

fun Project.toUpdateRequest(): UpdateProjectRequestDto = UpdateProjectRequestDto(
    title = title,
    description = description,
    status = status.name,
    investmentAmount = investmentAmount,
    obtainedProfit = obtainedProfit,
    productivityGainPercent = productivityGainPercent,
    deadline = deadlineEpochMillis.toIsoInstant(),
    guidelineId = guidelineId
)

internal fun String.toProjectStatus(): ProjectStatus =
    enumValues<ProjectStatus>().firstOrNull { it.name == this } ?: ProjectStatus.BACKLOG
