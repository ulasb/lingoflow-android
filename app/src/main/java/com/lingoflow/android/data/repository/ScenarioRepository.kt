package com.lingoflow.android.data.repository

import com.lingoflow.android.data.dao.ScenarioDao
import com.lingoflow.android.data.entity.ScenarioEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ScenarioRepository @Inject constructor(
    private val scenarioDao: ScenarioDao
) {
    fun observeAll(): Flow<List<ScenarioEntity>> = scenarioDao.observeAll()

    suspend fun getAll(): List<ScenarioEntity> = scenarioDao.getAll()

    suspend fun getById(id: String): ScenarioEntity? = scenarioDao.getById(id)

    suspend fun replaceAll(scenarios: List<ScenarioEntity>) {
        scenarioDao.replaceAll(scenarios)
    }

    suspend fun insert(scenarios: List<ScenarioEntity>) {
        scenarioDao.insertAll(scenarios)
    }

    suspend fun deleteById(id: String) {
        scenarioDao.deleteById(id)
    }

    suspend fun count(): Int = scenarioDao.count()
}
