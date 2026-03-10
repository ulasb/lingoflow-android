package com.lingoflow.android.data.repository

import com.lingoflow.android.data.dao.HistoryDao
import com.lingoflow.android.data.dao.MessageDao
import com.lingoflow.android.data.entity.HistoryEntity
import com.lingoflow.android.data.entity.MessageEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HistoryRepository @Inject constructor(
    private val historyDao: HistoryDao,
    private val messageDao: MessageDao
) {
    fun observeCompleted(): Flow<List<HistoryEntity>> = historyDao.observeCompleted()

    suspend fun getById(id: Long): HistoryEntity? = historyDao.getById(id)

    suspend fun getOrCreateForScenario(
        scenarioId: String,
        practiceLanguage: String,
        model: String
    ): Long {
        return historyDao.getIncompleteForScenario(scenarioId)
            ?: historyDao.insert(
                HistoryEntity(
                    scenarioId = scenarioId,
                    practiceLanguage = practiceLanguage,
                    model = model
                )
            )
    }

    suspend fun appendMessage(historyId: Long, speaker: String, content: String) {
        messageDao.insert(
            MessageEntity(historyId = historyId, speaker = speaker, content = content)
        )
    }

    fun observeMessages(historyId: Long): Flow<List<MessageEntity>> =
        messageDao.observeForHistory(historyId)

    suspend fun getMessages(historyId: Long): List<MessageEntity> =
        messageDao.getForHistory(historyId)

    suspend fun markCompleted(historyId: Long) {
        historyDao.markCompleted(historyId)
    }

    suspend fun saveSummary(historyId: Long, summary: String) {
        historyDao.saveSummary(historyId, summary)
    }

    suspend fun deleteById(id: Long) {
        historyDao.deleteById(id)
    }

    suspend fun deleteAllCompleted() {
        historyDao.deleteAllCompleted()
    }

    suspend fun abandonConversation(historyId: Long) {
        messageDao.deleteForHistory(historyId)
        historyDao.deleteById(historyId)
    }
}
