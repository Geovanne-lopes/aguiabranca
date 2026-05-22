package br.com.fiap.challengeaguiabranca.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import br.com.fiap.challengeaguiabranca.data.local.converter.RoomConverters
import br.com.fiap.challengeaguiabranca.data.local.dao.IdeaDao
import br.com.fiap.challengeaguiabranca.data.local.dao.ProjectDao
import br.com.fiap.challengeaguiabranca.data.local.dao.SessionDao
import br.com.fiap.challengeaguiabranca.data.local.dao.StrategicGuidelineDao
import br.com.fiap.challengeaguiabranca.data.local.entity.IdeaEntity
import br.com.fiap.challengeaguiabranca.data.local.entity.ProjectEntity
import br.com.fiap.challengeaguiabranca.data.local.entity.SessionEntity
import br.com.fiap.challengeaguiabranca.data.local.entity.StrategicGuidelineEntity

@Database(
    entities = [
        IdeaEntity::class,
        ProjectEntity::class,
        StrategicGuidelineEntity::class,
        SessionEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(RoomConverters::class)
abstract class InnovationDatabase : RoomDatabase() {

    abstract fun ideaDao(): IdeaDao
    abstract fun projectDao(): ProjectDao
    abstract fun strategicGuidelineDao(): StrategicGuidelineDao
    abstract fun sessionDao(): SessionDao
}
