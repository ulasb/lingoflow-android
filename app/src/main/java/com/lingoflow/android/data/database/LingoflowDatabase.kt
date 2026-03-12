package com.lingoflow.android.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.lingoflow.android.data.dao.HistoryDao
import com.lingoflow.android.data.dao.MessageDao
import com.lingoflow.android.data.dao.ScenarioDao
import com.lingoflow.android.data.dao.SettingsDao
import com.lingoflow.android.data.entity.HistoryEntity
import com.lingoflow.android.data.entity.MessageEntity
import com.lingoflow.android.data.entity.ScenarioEntity
import com.lingoflow.android.data.entity.SettingsEntity

@Database(
    entities = [
        SettingsEntity::class,
        ScenarioEntity::class,
        HistoryEntity::class,
        MessageEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class LingoflowDatabase : RoomDatabase() {
    abstract fun settingsDao(): SettingsDao
    abstract fun scenarioDao(): ScenarioDao
    abstract fun historyDao(): HistoryDao
    abstract fun messageDao(): MessageDao
}
