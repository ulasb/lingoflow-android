package com.lingoflow.android.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.lingoflow.android.data.entity.MessageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageDao {
    @Insert
    suspend fun insert(message: MessageEntity): Long

    @Query("SELECT * FROM messages WHERE historyId = :historyId ORDER BY id ASC")
    fun observeForHistory(historyId: Long): Flow<List<MessageEntity>>

    @Query("SELECT * FROM messages WHERE historyId = :historyId ORDER BY id ASC")
    suspend fun getForHistory(historyId: Long): List<MessageEntity>

    @Query("DELETE FROM messages WHERE historyId = :historyId")
    suspend fun deleteForHistory(historyId: Long)
}
