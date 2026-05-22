package br.com.fiap.challengeaguiabranca.data.repository

import br.com.fiap.challengeaguiabranca.data.local.dao.StrategicGuidelineDao
import br.com.fiap.challengeaguiabranca.data.local.mapper.GuidelineEntityMapper
import br.com.fiap.challengeaguiabranca.domain.model.StrategicGuideline
import br.com.fiap.challengeaguiabranca.domain.repository.GuidelineRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GuidelineRepositoryImpl(
    private val guidelineDao: StrategicGuidelineDao
) : GuidelineRepository {

    override fun observeAll(): Flow<List<StrategicGuideline>> =
        guidelineDao.observeAll().map(GuidelineEntityMapper::toDomainList)

    override suspend fun getById(id: String): StrategicGuideline? =
        guidelineDao.getById(id)?.let(GuidelineEntityMapper::toDomain)

    override suspend fun insert(guideline: StrategicGuideline) {
        guidelineDao.insert(GuidelineEntityMapper.toEntity(guideline))
    }

    override suspend fun update(guideline: StrategicGuideline) {
        guidelineDao.update(GuidelineEntityMapper.toEntity(guideline))
    }

    override suspend fun delete(id: String) {
        val entity = guidelineDao.getById(id) ?: return
        guidelineDao.delete(entity)
    }
}
