package br.com.fiap.challengeaguiabranca.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class IdeaResponseDto(
    val id: String,
    val title: String,
    val description: String,
    val category: String,
    val authorId: String,
    val authorName: String? = null,
    val status: String,
    val guidelineId: String? = null,
    val guidelineTitle: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null
)

@Serializable
data class CreateIdeaRequestDto(
    val title: String,
    val description: String,
    val category: String,
    val guidelineId: String? = null
)

@Serializable
data class UpdateIdeaStatusRequestDto(
    val status: String,
    val justification: String? = null
)

@Serializable
data class ProjectResponseDto(
    val id: String,
    val ideaId: String,
    val guidelineId: String? = null,
    val guidelineTitle: String? = null,
    val title: String,
    val description: String = "",
    val status: String,
    val investmentAmount: Double = 0.0,
    val obtainedProfit: Double = 0.0,
    val productivityGainPercent: Double = 0.0,
    val roiPercent: Double = 0.0,
    val deadline: String? = null,
    val managerId: String = "",
    val managerName: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null
)

@Serializable
data class CreateProjectRequestDto(
    val ideaId: String
)

@Serializable
data class UpdateProjectRequestDto(
    val title: String,
    val description: String,
    val status: String,
    val investmentAmount: Double,
    val obtainedProfit: Double,
    val productivityGainPercent: Double,
    val deadline: String? = null,
    val guidelineId: String? = null
)

@Serializable
data class GuidelineResponseDto(
    val id: String,
    val title: String,
    val content: String,
    val category: String? = null,
    val campaign: String? = null,
    val version: Int = 1,
    val authorId: String,
    val authorName: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null
)

@Serializable
data class GuidelineHistoryResponseDto(
    val id: String,
    val guidelineId: String = "",
    val action: String = "",
    val category: String? = null,
    val campaign: String? = null,
    val occurredAt: String? = null
)

@Serializable
data class GuidelineWriteRequestDto(
    val title: String,
    val content: String,
    val category: String? = null,
    val campaign: String? = null
)

@Serializable
data class SuggestionResponseDto(
    val id: String,
    val authorUserId: String = "",
    val authorName: String = "",
    val targetUserId: String,
    val targetEmail: String = "",
    val targetName: String = "",
    val message: String,
    val createdAt: String? = null
)

@Serializable
data class CreateSuggestionRequestDto(
    val targetUserId: String,
    val message: String
)

@Serializable
data class DailyInsightResponseDto(
    val id: Int = 0,
    val message: String = ""
)
