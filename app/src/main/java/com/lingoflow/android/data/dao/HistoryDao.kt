package com.lingoflow.android.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.lingoflow.android.data.entity.HistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {
    @Insert
    suspend fun insert(history: HistoryEntity): Long

    @Query("SELECT * FROM history WHERE completed = 1 ORDER BY id DESC")
    fun observeCompleted(): Flow<List<HistoryEntity>>

    @Query("SELECT * FROM history WHERE completed = 1 ORDER BY id DESC")
    suspend fun getCompleted(): List<HistoryEntity>

    @Query("SELECT * FROM history WHERE id = :id")
    suspend fun getById(id: Long): HistoryEntity?

    @Query("SELECT id FROM history WHERE scenarioId = :scenarioId AND completed = 0 ORDER BY id DESC LIMIT 1")
    suspend fun getIncompleteForScenario(scenarioId: String): Long?

    @Query("UPDATE history SET completed = 1 WHERE id = :id")
    suspend fun markCompleted(id: Long)

    @Query("UPDATE history SET summary = :summary WHERE id = :id")
    suspend fun saveSummary(id: Long, summary: String)

    @Query("DELETE FROM history WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM history WHERE completed = 1")
    suspend fun deleteAllCompleted()
}
