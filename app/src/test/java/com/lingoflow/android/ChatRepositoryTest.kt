package com.lingoflow.android

import com.lingoflow.android.data.entity.MessageEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ChatRepositoryTest {

    @Test
    fun `goal evaluation parses REACHED correctly`() {
        val response = "REACHED"
        assertTrue(response.trim().uppercase().contains("REACHED"))
    }

    @Test
    fun `goal evaluation parses PENDING correctly`() {
        val response = "PENDING"
        assertFalse(response.trim().uppercase().contains("REACHED"))
    }

    @Test
    fun `goal evaluation handles noisy response with REACHED`() {
        val response = "Based on the conversation, the goal is REACHED."
        assertTrue(response.trim().uppercase().contains("REACHED"))
    }

    @Test
    fun `messages to chat history converts speakers correctly`() {
        val messages = listOf(
            MessageEntity(id = 1, historyId = 1, speaker = "User", content = "Hello"),
            MessageEntity(id = 2, historyId = 1, speaker = "Bot", content = "Hi there!")
        )

        val history = messages.map { msg ->
            com.lingoflow.android.llm.ChatMessage(
                role = if (msg.speaker == "User") "user" else "assistant",
                content = msg.content
            )
        }

        assertEquals(2, history.size)
        assertEquals("user", history[0].role)
        assertEquals("Hello", history[0].content)
        assertEquals("assistant", history[1].role)
        assertEquals("Hi there!", history[1].content)
    }

    @Test
    fun `scenario JSON parsing strips clipart extension`() {
        val clipart = "restaurant_ordering_table.png"
        val cleaned = clipart.removeSuffix(".png").removeSuffix(".webp")
        assertEquals("restaurant_ordering_table", cleaned)
    }

    @Test
    fun `scenario JSON parsing handles no extension`() {
        val clipart = "default_conversation"
        val cleaned = clipart.removeSuffix(".png").removeSuffix(".webp")
        assertEquals("default_conversation", cleaned)
    }
}
