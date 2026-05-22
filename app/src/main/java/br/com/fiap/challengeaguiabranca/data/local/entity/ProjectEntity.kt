package br.com.fiap.challengeaguiabranca.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import br.com.fiap.challengeaguiabranca.domain.model.ProjectStatus

@Entity(tableName = "projects")
data class ProjectEntity(
    @PrimaryKey val id: String,
    val ideaId: String,
    val title: String,
    val description: String,
    val status: ProjectStatus,
    val investmentAmount: Double,
    val obtainedProfit: Double,
    val productivityGainPercent: Double,
    val deadlineEpochMillis: Long?,
    val managerId: String,
    val createdAtEpochMillis: Long,
    val updatedAtEpochMillis: Long
)
