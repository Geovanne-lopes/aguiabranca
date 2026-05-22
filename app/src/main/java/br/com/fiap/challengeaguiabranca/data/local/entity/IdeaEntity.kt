package br.com.fiap.challengeaguiabranca.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import br.com.fiap.challengeaguiabranca.domain.model.IdeaCategory
import br.com.fiap.challengeaguiabranca.domain.model.IdeaStatus

@Entity(tableName = "ideas")
data class IdeaEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val category: IdeaCategory,
    val authorId: String,
    val status: IdeaStatus,
    val createdAtEpochMillis: Long
)
