package com.lingoflow.android.llm

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LlmClientProvider @Inject constructor(
    private val mediaPipeLlmClient: MediaPipeLlmClient,
    private val geminiApiClient: GeminiApiClient
) {
    fun get(modelType: String): LlmClient {
        return when (modelType) {
            "cloud" -> geminiApiClient
            else -> mediaPipeLlmClient
        }
    }
}
