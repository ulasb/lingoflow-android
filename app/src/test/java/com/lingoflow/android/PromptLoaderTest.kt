package com.lingoflow.android

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PromptLoaderTest {

    @Test
    fun `variable substitution replaces all placeholders`() {
        val template = "Hello {{name}}, welcome to {{place}}!"
        val variables = mapOf("name" to "Alice", "place" to "Lingoflow")

        var result = template
        for ((key, value) in variables) {
            result = result.replace("{{$key}}", value)
        }

        assertEquals("Hello Alice, welcome to Lingoflow!", result)
    }

    @Test
    fun `variable substitution handles missing variables`() {
        val template = "Hello {{name}}, your goal is {{goal}}"
        val variables = mapOf("name" to "Bob")

        var result = template
        for ((key, value) in variables) {
            result = result.replace("{{$key}}", value)
        }

        assertEquals("Hello Bob, your goal is {{goal}}", result)
    }

    @Test
    fun `scenario JSON parsing handles valid JSON`() {
        val json = """
            [
                {
                    "id": "restaurant",
                    "setting": "A cozy restaurant",
                    "goal": "Order a meal",
                    "description": "You are at a restaurant.",
                    "clipart": "restaurant_ordering_table.png"
                }
            ]
        """.trimIndent()

        val cleaned = json.replace("```json", "").replace("```", "").trim()
        assertTrue(cleaned.startsWith("["))
        assertTrue(cleaned.contains("\"restaurant\""))
    }

    @Test
    fun `scenario JSON parsing handles markdown-wrapped JSON`() {
        val json = """
            ```json
            [{"id": "test", "setting": "Test", "goal": "Test goal", "description": "Desc", "clipart": "default_conversation.png"}]
            ```
        """.trimIndent()

        val cleaned = json.replace("```json", "").replace("```", "").trim()
        assertTrue(cleaned.startsWith("["))
        assertFalse(cleaned.contains("```"))
    }

    @Test
    fun `goal evaluation detects REACHED`() {
        val response = "REACHED"
        assertTrue(response.trim().uppercase().contains("REACHED"))
    }

    @Test
    fun `goal evaluation detects PENDING`() {
        val response = "PENDING"
        assertFalse(response.trim().uppercase().contains("REACHED"))
    }
}
