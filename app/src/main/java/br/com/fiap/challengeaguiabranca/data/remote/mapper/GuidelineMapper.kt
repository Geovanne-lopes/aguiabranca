package br.com.fiap.challengeaguiabranca.data.remote.mapper

import br.com.fiap.challengeaguiabranca.data.remote.dto.GuidelineHistoryResponseDto
import br.com.fiap.challengeaguiabranca.data.remote.dto.GuidelineResponseDto
import br.com.fiap.challengeaguiabranca.data.remote.dto.GuidelineWriteRequestDto
import br.com.fiap.challengeaguiabranca.domain.model.GuidelineHistoryEntry
import br.com.fiap.challengeaguiabranca.domain.model.StrategicGuideline

fun GuidelineResponseDto.toDomain(): StrategicGuideline = StrategicGuideline(
    id = id,
    title = title,
    content = content,
    authorId = authorId,
    createdAtEpochMillis = createdAt.toEpochMillis(),
    updatedAtEpochMillis = updatedAt.toEpochMillis(),
    category = category,
    campaign = campaign,
    version = version
)

fun GuidelineHistoryResponseDto.toDomain(): GuidelineHistoryEntry = GuidelineHistoryEntry(
    id = id,
    occurredAtEpochMillis = occurredAt.toEpochMillis(),
    category = category,
    campaign = campaign,
    action = action
)

fun StrategicGuideline.toWriteRequest(): GuidelineWriteRequestDto = GuidelineWriteRequestDto(
    title = title,
    content = content,
    category = category,
    campaign = campaign
)
