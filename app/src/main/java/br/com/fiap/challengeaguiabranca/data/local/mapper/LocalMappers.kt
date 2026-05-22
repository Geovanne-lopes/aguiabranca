package br.com.fiap.challengeaguiabranca.data.local.mapper

import br.com.fiap.challengeaguiabranca.data.local.entity.IdeaEntity
import br.com.fiap.challengeaguiabranca.data.local.entity.ProjectEntity
import br.com.fiap.challengeaguiabranca.data.local.entity.SessionEntity
import br.com.fiap.challengeaguiabranca.data.local.entity.StrategicGuidelineEntity
import br.com.fiap.challengeaguiabranca.domain.model.Idea
import br.com.fiap.challengeaguiabranca.domain.model.Project
import br.com.fiap.challengeaguiabranca.domain.model.StrategicGuideline
import br.com.fiap.challengeaguiabranca.domain.model.User

object IdeaEntityMapper {

    fun toDomain(entity: IdeaEntity): Idea = Idea(
        id = entity.id,
        title = entity.title,
        description = entity.description,
        category = entity.category,
        authorId = entity.authorId,
        status = entity.status,
        createdAtEpochMillis = entity.createdAtEpochMillis
    )

    fun toEntity(domain: Idea): IdeaEntity = IdeaEntity(
        id = domain.id,
        title = domain.title,
        description = domain.description,
        category = domain.category,
        authorId = domain.authorId,
        status = domain.status,
        createdAtEpochMillis = domain.createdAtEpochMillis
    )

    fun toDomainList(entities: List<IdeaEntity>): List<Idea> =
        entities.map(::toDomain)
}

object ProjectEntityMapper {

    fun toDomain(entity: ProjectEntity): Project = Project(
        id = entity.id,
        ideaId = entity.ideaId,
        title = entity.title,
        description = entity.description,
        status = entity.status,
        investmentAmount = entity.investmentAmount,
        obtainedProfit = entity.obtainedProfit,
        productivityGainPercent = entity.productivityGainPercent,
        deadlineEpochMillis = entity.deadlineEpochMillis,
        managerId = entity.managerId,
        createdAtEpochMillis = entity.createdAtEpochMillis,
        updatedAtEpochMillis = entity.updatedAtEpochMillis
    )

    fun toEntity(domain: Project): ProjectEntity = ProjectEntity(
        id = domain.id,
        ideaId = domain.ideaId,
        title = domain.title,
        description = domain.description,
        status = domain.status,
        investmentAmount = domain.investmentAmount,
        obtainedProfit = domain.obtainedProfit,
        productivityGainPercent = domain.productivityGainPercent,
        deadlineEpochMillis = domain.deadlineEpochMillis,
        managerId = domain.managerId,
        createdAtEpochMillis = domain.createdAtEpochMillis,
        updatedAtEpochMillis = domain.updatedAtEpochMillis
    )

    fun toDomainList(entities: List<ProjectEntity>): List<Project> =
        entities.map(::toDomain)
}

object GuidelineEntityMapper {

    fun toDomain(entity: StrategicGuidelineEntity): StrategicGuideline = StrategicGuideline(
        id = entity.id,
        title = entity.title,
        content = entity.content,
        authorId = entity.authorId,
        createdAtEpochMillis = entity.createdAtEpochMillis,
        updatedAtEpochMillis = entity.updatedAtEpochMillis
    )

    fun toEntity(domain: StrategicGuideline): StrategicGuidelineEntity = StrategicGuidelineEntity(
        id = domain.id,
        title = domain.title,
        content = domain.content,
        authorId = domain.authorId,
        createdAtEpochMillis = domain.createdAtEpochMillis,
        updatedAtEpochMillis = domain.updatedAtEpochMillis
    )

    fun toDomainList(entities: List<StrategicGuidelineEntity>): List<StrategicGuideline> =
        entities.map(::toDomain)
}

object SessionEntityMapper {

    fun toDomain(entity: SessionEntity): User = User(
        id = entity.userId,
        name = entity.name,
        email = entity.email,
        avatarUrl = entity.avatarUrl,
        role = entity.role
    )

    fun toEntity(user: User): SessionEntity = SessionEntity(
        userId = user.id,
        name = user.name,
        email = user.email,
        avatarUrl = user.avatarUrl,
        role = user.role
    )
}
