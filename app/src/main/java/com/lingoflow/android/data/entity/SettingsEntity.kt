package com.lingoflow.android.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "settings")
data class SettingsEntity(
    @PrimaryKey val id: Int = 1,
    val theme: String = "system",
    val modelType: String = "on_device", // "on_device" or "cloud"
    val cloudModel: String = "gemini-2.5-flash",
    val practiceLanguage: String = "Japanese",
    val uiLanguage: String = "English",
    val score: Int = 0
)
