package com.lingoflow.android

import com.lingoflow.android.llm.ChatMessage
import com.lingoflow.android.llm.LlmClient
import com.lingoflow.android.llm.LlmToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FakeLlmClient(private val response: String) : LlmClient {
    var lastSystemPrompt: String? = null
    var lastUserMessage: String? = null
    var lastHistory: List<ChatMessage>? = null

    override fun generate(
        systemPrompt: String,
        history: List<ChatMessage>,
        userMessage: String
    ): Flow<LlmToken> = flow {
        lastSystemPrompt = systemPrompt
        lastHistory = history
        lastUserMessage = userMessage
        emit(LlmToken.Content(response))
        emit(LlmToken.Done)
    }

    override suspend fun generateBlocking(
        systemPrompt: String,
        history: List<ChatMessage>,
        userMessage: String
    ): String {
        lastSystemPrompt = systemPrompt
        lastHistory = history
        lastUserMessage = userMessage
        return response
    }
}

class LlmClientTest {

    @Test
    fun `fake client returns configured response`() = runTest {
        val client = FakeLlmClient("Hello!")
        val result = client.generateBlocking("system", emptyList(), "Hi")
        assertEquals("Hello!", result)
    }

    @Test
    fun `fake client streams tokens correctly`() = runTest {
        val client = FakeLlmClient("Streamed response")
        val tokens = client.generate("system", emptyList(), "Hi").toList()

        assertEquals(2, tokens.size)
        assertTrue(tokens[0] is LlmToken.Content)
        assertEquals("Streamed response", (tokens[0] as LlmToken.Content).text)
        assertTrue(tokens[1] is LlmToken.Done)
    }

    @Test
    fun `fake client captures parameters`() = runTest {
        val client = FakeLlmClient("response")
        val history = listOf(ChatMessage("user", "previous message"))

        client.generateBlocking("my system prompt", history, "new message")

        assertEquals("my system prompt", client.lastSystemPrompt)
        assertEquals("new message", client.lastUserMessage)
        assertEquals(1, client.lastHistory?.size)
    }
}
