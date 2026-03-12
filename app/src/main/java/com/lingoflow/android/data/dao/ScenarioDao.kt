package com.lingoflow.android.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.lingoflow.android.data.entity.ScenarioEntity
import kotlinx.coroutines.flow.Flow

@Dao
abstract class ScenarioDao {

    @Transaction
    open suspend fun replaceAll(scenarios: List<ScenarioEntity>) {
        deleteAll()
        insertAll(scenarios)
    }

    @Query("SELECT * FROM active_scenarios")
    abstract fun observeAll(): Flow<List<ScenarioEntity>>

    @Query("SELECT * FROM active_scenarios")
    abstract suspend fun getAll(): List<ScenarioEntity>

    @Query("SELECT * FROM active_scenarios WHERE id = :id")
    abstract suspend fun getById(id: String): ScenarioEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun insertAll(scenarios: List<ScenarioEntity>)

    @Query("DELETE FROM active_scenarios")
    abstract suspend fun deleteAll()

    @Query("DELETE FROM active_scenarios WHERE id = :id")
    abstract suspend fun deleteById(id: String)

    @Query("SELECT COUNT(*) FROM active_scenarios")
    abstract suspend fun count(): Int
}
