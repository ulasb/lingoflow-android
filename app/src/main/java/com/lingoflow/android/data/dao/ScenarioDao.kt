package com.lingoflow.android.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.lingoflow.android.data.entity.ScenarioEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ScenarioDao {
    @Query("SELECT * FROM active_scenarios")
    fun observeAll(): Flow<List<ScenarioEntity>>

    @Query("SELECT * FROM active_scenarios")
    suspend fun getAll(): List<ScenarioEntity>

    @Query("SELECT * FROM active_scenarios WHERE id = :id")
    suspend fun getById(id: String): ScenarioEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(scenarios: List<ScenarioEntity>)

    @Query("DELETE FROM active_scenarios")
    suspend fun deleteAll()

    @Query("DELETE FROM active_scenarios WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("SELECT COUNT(*) FROM active_scenarios")
    suspend fun count(): Int
}
