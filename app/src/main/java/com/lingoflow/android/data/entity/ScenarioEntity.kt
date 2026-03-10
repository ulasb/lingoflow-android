package com.lingoflow.android.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "active_scenarios")
data class ScenarioEntity(
    @PrimaryKey val id: String,
    val setting: String,
    val goal: String,
    val description: String = "",
    val clipart: String = "default_conversation"
)
