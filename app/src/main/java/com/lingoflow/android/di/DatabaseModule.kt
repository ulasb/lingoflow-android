package com.lingoflow.android.di

import android.content.Context
import androidx.room.Room
import com.lingoflow.android.data.dao.HistoryDao
import com.lingoflow.android.data.dao.MessageDao
import com.lingoflow.android.data.dao.ScenarioDao
import com.lingoflow.android.data.dao.SettingsDao
import com.lingoflow.android.data.database.LingoflowDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): LingoflowDatabase {
        return Room.databaseBuilder(
            context,
            LingoflowDatabase::class.java,
            "lingoflow.db"
        ).build()
    }

    @Provides fun provideSettingsDao(db: LingoflowDatabase): SettingsDao = db.settingsDao()
    @Provides fun provideScenarioDao(db: LingoflowDatabase): ScenarioDao = db.scenarioDao()
    @Provides fun provideHistoryDao(db: LingoflowDatabase): HistoryDao = db.historyDao()
    @Provides fun provideMessageDao(db: LingoflowDatabase): MessageDao = db.messageDao()
}
