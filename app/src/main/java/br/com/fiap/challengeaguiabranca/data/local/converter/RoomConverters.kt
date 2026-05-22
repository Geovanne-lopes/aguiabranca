package br.com.fiap.challengeaguiabranca.data.local.converter

import androidx.room.TypeConverter
import br.com.fiap.challengeaguiabranca.domain.model.IdeaCategory
import br.com.fiap.challengeaguiabranca.domain.model.IdeaStatus
import br.com.fiap.challengeaguiabranca.domain.model.ProjectStatus
import br.com.fiap.challengeaguiabranca.domain.model.UserRole

class RoomConverters {

    @TypeConverter
    fun fromIdeaStatus(value: String): IdeaStatus = IdeaStatus.valueOf(value)

    @TypeConverter
    fun toIdeaStatus(status: IdeaStatus): String = status.name

    @TypeConverter
    fun fromIdeaCategory(value: String): IdeaCategory = IdeaCategory.valueOf(value)

    @TypeConverter
    fun toIdeaCategory(category: IdeaCategory): String = category.name

    @TypeConverter
    fun fromProjectStatus(value: String): ProjectStatus = ProjectStatus.valueOf(value)

    @TypeConverter
    fun toProjectStatus(status: ProjectStatus): String = status.name

    @TypeConverter
    fun fromUserRole(value: String): UserRole = UserRole.valueOf(value)

    @TypeConverter
    fun toUserRole(role: UserRole): String = role.name
}
