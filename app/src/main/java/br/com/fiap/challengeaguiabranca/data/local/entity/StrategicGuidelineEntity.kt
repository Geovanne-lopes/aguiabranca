package br.com.fiap.challengeaguiabranca.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "strategic_guidelines")
data class StrategicGuidelineEntity(
    @PrimaryKey val id: String,
    val title: String,
    val content: String,
    val authorId: String,
    val createdAtEpochMillis: Long,
    val updatedAtEpochMillis: Long
)
