package com.lingoflow.android.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "history")
data class HistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val scenarioId: String,
    val completed: Boolean = false,
    val summary: String? = null,
    val practiceLanguage: String? = null,
    val model: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)
