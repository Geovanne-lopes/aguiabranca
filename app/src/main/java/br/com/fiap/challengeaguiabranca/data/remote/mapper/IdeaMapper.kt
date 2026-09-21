package br.com.fiap.challengeaguiabranca.data.remote.mapper

import br.com.fiap.challengeaguiabranca.data.remote.dto.CreateIdeaRequestDto
import br.com.fiap.challengeaguiabranca.data.remote.dto.IdeaResponseDto
import br.com.fiap.challengeaguiabranca.domain.model.Idea
import br.com.fiap.challengeaguiabranca.domain.model.IdeaCategory
import br.com.fiap.challengeaguiabranca.domain.model.IdeaStatus

fun IdeaResponseDto.toDomain(): Idea = Idea(
    id = id,
    title = title,
    description = description,
    category = category.toIdeaCategory(),
    authorId = authorId,
    status = status.toIdeaStatus(),
    createdAtEpochMillis = createdAt.toEpochMillis(),
    guidelineId = guidelineId,
    guidelineTitle = guidelineTitle,
    authorName = authorName
)

fun Idea.toCreateRequest(): CreateIdeaRequestDto = CreateIdeaRequestDto(
    title = title,
    description = description,
    category = category.name,
    guidelineId = guidelineId
)

internal fun String.toIdeaCategory(): IdeaCategory =
    enumValues<IdeaCategory>().firstOrNull { it.name == this } ?: IdeaCategory.OTHER

internal fun String.toIdeaStatus(): IdeaStatus =
    enumValues<IdeaStatus>().firstOrNull { it.name == this } ?: IdeaStatus.PENDING
