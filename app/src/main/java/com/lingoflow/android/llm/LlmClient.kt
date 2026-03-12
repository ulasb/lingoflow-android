package com.lingoflow.android.llm

import kotlinx.coroutines.flow.Flow

sealed class LlmToken {
    data class Content(val text: String) : LlmToken()
    data object Done : LlmToken()
    data class Error(val cause: Throwable) : LlmToken()
}

data class ChatMessage(
    val role: String, // "user" or "assistant"
    val content: String
)

interface LlmClient {
    fun generate(
        systemPrompt: String,
        history: List<ChatMessage>,
        userMessage: String
    ): Flow<LlmToken>

    suspend fun generateBlocking(
        systemPrompt: String,
        history: List<ChatMessage> = emptyList(),
        userMessage: String
    ): String
}
