package br.com.fiap.challengeaguiabranca.domain.repository

import br.com.fiap.challengeaguiabranca.domain.model.StrategicGuideline
import kotlinx.coroutines.flow.Flow

interface GuidelineRepository {
    fun observeAll(): Flow<List<StrategicGuideline>>
    suspend fun getById(id: String): StrategicGuideline?
    suspend fun insert(guideline: StrategicGuideline)
    suspend fun update(guideline: StrategicGuideline)
    suspend fun delete(id: String)
}
