package com.lingoflow.android

import android.app.Application
import com.lingoflow.android.llm.MediaPipeLlmClient
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class LingoflowApp : Application() {

    @Inject lateinit var mediaPipeLlmClient: MediaPipeLlmClient

    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    override fun onCreate() {
        super.onCreate()
        // Eagerly load on-device model in background if available
        appScope.launch {
            if (mediaPipeLlmClient.isModelAvailable) {
                mediaPipeLlmClient.loadModel()
            }
        }
    }
}
