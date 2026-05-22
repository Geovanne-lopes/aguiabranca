package br.com.fiap.challengeaguiabranca.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import br.com.fiap.challengeaguiabranca.data.local.entity.IdeaEntity
import br.com.fiap.challengeaguiabranca.domain.model.IdeaStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface IdeaDao {

    @Query("SELECT * FROM ideas ORDER BY createdAtEpochMillis DESC")
    fun observeAll(): Flow<List<IdeaEntity>>

    @Query("SELECT * FROM ideas WHERE authorId = :authorId ORDER BY createdAtEpochMillis DESC")
    fun observeByAuthor(authorId: String): Flow<List<IdeaEntity>>

    @Query("SELECT * FROM ideas WHERE status = :status ORDER BY createdAtEpochMillis DESC")
    fun observeByStatus(status: IdeaStatus): Flow<List<IdeaEntity>>

    @Query("SELECT * FROM ideas WHERE id = :id LIMIT 1")
    suspend fun getById(id: String): IdeaEntity?

    @Query("SELECT COUNT(*) FROM ideas WHERE status = :status")
    suspend fun countByStatus(status: IdeaStatus): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(idea: IdeaEntity)

    @Update
    suspend fun update(idea: IdeaEntity)

    @Query("UPDATE ideas SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: String, status: IdeaStatus)

    @Delete
    suspend fun delete(idea: IdeaEntity)
}
