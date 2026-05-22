package br.com.fiap.challengeaguiabranca.di

import androidx.room.Room
import br.com.fiap.challengeaguiabranca.data.local.DatabaseSeeder
import br.com.fiap.challengeaguiabranca.data.local.InnovationDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val databaseModule = module {

    single {
        Room.databaseBuilder(
            androidContext(),
            InnovationDatabase::class.java,
            "innovation_database"
        ).fallbackToDestructiveMigration(dropAllTables = true)
            .build()
    }

    single { get<InnovationDatabase>().ideaDao() }
    single { get<InnovationDatabase>().projectDao() }
    single { get<InnovationDatabase>().strategicGuidelineDao() }
    single { get<InnovationDatabase>().sessionDao() }

    single { DatabaseSeeder(get()) }
}
