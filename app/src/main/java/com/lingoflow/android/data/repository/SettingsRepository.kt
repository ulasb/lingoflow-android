package com.lingoflow.android.data.repository

import com.lingoflow.android.data.dao.SettingsDao
import com.lingoflow.android.data.entity.SettingsEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepository @Inject constructor(
    private val settingsDao: SettingsDao
) {
    fun observe(): Flow<SettingsEntity> = settingsDao.observe().filterNotNull()

    suspend fun get(): SettingsEntity {
        return settingsDao.get() ?: SettingsEntity().also { settingsDao.upsert(it) }
    }

    suspend fun update(settings: SettingsEntity) {
        settingsDao.upsert(settings)
    }

    suspend fun incrementScore(delta: Int = 1) {
        settingsDao.incrementScore(delta)
    }

    suspend fun ensureInitialized() {
        if (settingsDao.get() == null) {
            settingsDao.upsert(SettingsEntity())
        }
    }
}
