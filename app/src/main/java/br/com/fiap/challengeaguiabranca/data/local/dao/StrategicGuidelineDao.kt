package br.com.fiap.challengeaguiabranca.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import br.com.fiap.challengeaguiabranca.data.local.entity.StrategicGuidelineEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StrategicGuidelineDao {

    @Query("SELECT * FROM strategic_guidelines ORDER BY updatedAtEpochMillis DESC")
    fun observeAll(): Flow<List<StrategicGuidelineEntity>>

    @Query("SELECT * FROM strategic_guidelines WHERE id = :id LIMIT 1")
    suspend fun getById(id: String): StrategicGuidelineEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(guideline: StrategicGuidelineEntity)

    @Update
    suspend fun update(guideline: StrategicGuidelineEntity)

    @Delete
    suspend fun delete(guideline: StrategicGuidelineEntity)
}
