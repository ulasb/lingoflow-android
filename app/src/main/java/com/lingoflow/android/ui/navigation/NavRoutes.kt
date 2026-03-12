package com.lingoflow.android.ui.navigation

object NavRoutes {
    const val DASHBOARD = "dashboard"
    const val CHAT = "chat/{historyId}/{scenarioId}"
    const val SETTINGS = "settings"
    const val HISTORY = "history"
    const val HISTORY_DETAIL = "history_detail/{historyId}"
    const val ONBOARDING = "onboarding"
    const val MODEL_DOWNLOAD = "model_download"

    fun chat(historyId: Long, scenarioId: String) = "chat/$historyId/$scenarioId"
    fun historyDetail(historyId: Long) = "history_detail/$historyId"
}
