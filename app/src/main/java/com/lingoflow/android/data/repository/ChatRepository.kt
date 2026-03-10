package com.lingoflow.android.data.repository

import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lingoflow.android.data.entity.MessageEntity
import com.lingoflow.android.data.entity.ScenarioEntity
import com.lingoflow.android.llm.ChatMessage
import com.lingoflow.android.llm.LlmClient
import com.lingoflow.android.llm.LlmClientProvider
import com.lingoflow.android.llm.LlmToken
import com.lingoflow.android.llm.PromptLoader
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

data class TurnResult(
    val botMessage: String,
    val goalReached: Boolean,
    val summary: String? = null
)

@Singleton
class ChatRepository @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val scenarioRepository: ScenarioRepository,
    private val historyRepository: HistoryRepository,
    private val llmClientProvider: LlmClientProvider,
    private val promptLoader: PromptLoader,
    private val gson: Gson
) {
    companion object {
        private const val TAG = "ChatRepository"
    }

    private fun getClientForModelType(modelType: String): LlmClient {
        return llmClientProvider.get(modelType)
    }

    fun streamChatTurn(
        systemPrompt: String,
        history: List<ChatMessage>,
        userMessage: String,
        modelType: String
    ): Flow<LlmToken> {
        val client = getClientForModelType(modelType)
        return client.generate(systemPrompt, history, userMessage)
    }

    fun buildSystemPrompt(
        practiceLanguage: String,
        uiLanguage: String,
        setting: String,
        goal: String
    ): String {
        return promptLoader.load(
            "chat_system_prompt.txt",
            mapOf(
                "practice_language" to practiceLanguage,
                "ui_language" to uiLanguage,
                "scenario_setting" to setting,
                "scenario_goal" to goal
            )
        )
    }

    suspend fun evaluateGoal(
        modelType: String,
        goal: String,
        messages: List<MessageEntity>
    ): Boolean {
        val client = getClientForModelType(modelType)
        val historyText = messages.joinToString("\n") { "${it.speaker}: ${it.content}" }
        val prompt = promptLoader.load(
            "goal_evaluation.txt",
            mapOf(
                "scenario_goal" to goal,
                "conversation_history" to historyText
            )
        )
        return try {
            val response = client.generateBlocking(
                systemPrompt = "",
                userMessage = prompt
            )
            response.trim().uppercase().contains("REACHED")
        } catch (e: Exception) {
            Log.e(TAG, "Goal evaluation failed", e)
            false
        }
    }

    suspend fun generateSummary(
        modelType: String,
        practiceLanguage: String,
        uiLanguage: String,
        goal: String,
        messages: List<MessageEntity>
    ): String? {
        val client = getClientForModelType(modelType)
        val historyText = messages.joinToString("\n") { "${it.speaker}: ${it.content}" }
        val prompt = promptLoader.load(
            "conversation_summary.txt",
            mapOf(
                "practice_language" to practiceLanguage,
                "ui_language" to uiLanguage,
                "scenario_goal" to goal,
                "conversation_history" to historyText
            )
        )
        return try {
            client.generateBlocking(systemPrompt = "", userMessage = prompt)
        } catch (e: Exception) {
            Log.e(TAG, "Summary generation failed", e)
            null
        }
    }

    suspend fun generateHint(
        modelType: String,
        practiceLanguage: String,
        uiLanguage: String,
        setting: String,
        goal: String,
        messages: List<MessageEntity>
    ): String? {
        val client = getClientForModelType(modelType)
        val historyText = messages.joinToString("\n") { "${it.speaker}: ${it.content}" }
        val prompt = promptLoader.load(
            "hint_generation.txt",
            mapOf(
                "practice_language" to practiceLanguage,
                "ui_language" to uiLanguage,
                "scenario_setting" to setting,
                "scenario_goal" to goal,
                "conversation_history" to historyText
            )
        )
        return try {
            client.generateBlocking(systemPrompt = "", userMessage = prompt)
        } catch (e: Exception) {
            Log.e(TAG, "Hint generation failed", e)
            null
        }
    }

    suspend fun explainLine(
        modelType: String,
        practiceLanguage: String,
        uiLanguage: String,
        sentence: String
    ): String? {
        val client = getClientForModelType(modelType)
        val prompt = promptLoader.load(
            "line_explanation.txt",
            mapOf(
                "practice_language" to practiceLanguage,
                "ui_language" to uiLanguage,
                "sentence" to sentence
            )
        )
        return try {
            client.generateBlocking(systemPrompt = "", userMessage = prompt)
        } catch (e: Exception) {
            Log.e(TAG, "Line explanation failed", e)
            null
        }
    }

    suspend fun generateScenarios(
        modelType: String,
        practiceLanguage: String,
        uiLanguage: String,
        count: Int = 6
    ): List<ScenarioEntity> {
        val client = getClientForModelType(modelType)
        val prompt = promptLoader.load(
            "generate_scenarios.txt",
            mapOf(
                "practice_language" to practiceLanguage,
                "ui_language" to uiLanguage,
                "count" to count.toString()
            )
        )
        val response = client.generateBlocking(systemPrompt = "", userMessage = prompt)
        Log.d(TAG, "Scenario generation raw response (${response.length} chars):")
        // Log full response in chunks (logcat has a ~4000 char limit per line)
        response.chunked(3000).forEachIndexed { i, chunk ->
            Log.d(TAG, "Response chunk $i: $chunk")
        }
        val scenarios = parseScenarios(response)
        if (scenarios.isEmpty()) {
            throw RuntimeException("Could not parse scenarios from LLM response. Raw response: ${response.take(200)}")
        }
        return scenarios
    }

    private fun parseScenarios(response: String): List<ScenarioEntity> {
        // Extract JSON array from the response, stripping any surrounding text/markdown
        val jsonArray = extractJsonArray(response)

        if (jsonArray != null) {
            // Normalize the JSON: quote unquoted keys so Gson can parse it
            val normalized = normalizeJson(jsonArray)
            Log.d(TAG, "Normalized JSON (${normalized.length} chars): ${normalized.take(500)}")

            try {
                val type = object : TypeToken<List<Map<String, String>>>() {}.type
                val reader = com.google.gson.stream.JsonReader(java.io.StringReader(normalized))
                reader.isLenient = true
                val list: List<Map<String, String>> = gson.fromJson(reader, type)
                val results = list.map { map ->
                    val clipart = (map["clipart"] ?: "default_conversation")
                        .removeSuffix(".png")
                        .removeSuffix(".webp")
                    ScenarioEntity(
                        id = map["id"] ?: "scenario_${System.currentTimeMillis()}",
                        setting = map["setting"] ?: "",
                        goal = map["goal"] ?: "",
                        description = map["description"] ?: "",
                        clipart = clipart
                    )
                }
                if (results.isNotEmpty()) return results
            } catch (e: Exception) {
                Log.e(TAG, "Gson parse failed, trying manual extraction", e)
            }
        }

        // Fallback: manually extract whatever complete objects exist (handles truncated JSON)
        val fromText = response.substring(response.indexOf('[').coerceAtLeast(0))
        val manual = manualParseScenarios(fromText)
        Log.d(TAG, "Manual parse found ${manual.size} scenarios")
        return manual
    }

    private fun extractJsonArray(response: String): String? {
        val start = response.indexOf('[')
        val end = response.lastIndexOf(']')
        if (start == -1 || end == -1 || end <= start) return null
        return response.substring(start, end + 1)
    }

    private fun normalizeJson(json: String): String {
        // Quote unquoted keys: only match keys NOT already preceded by a double-quote
        return json.replace(Regex("""([{,]\s*)(?!")([a-zA-Z_]\w*)\s*:""")) { match ->
            "${match.groupValues[1]}\"${match.groupValues[2]}\":"
        }
    }

    private fun manualParseScenarios(text: String): List<ScenarioEntity> {
        // Use brace-counting to extract top-level objects from the text
        val scenarios = mutableListOf<ScenarioEntity>()
        val objects = extractJsonObjects(text)
        for (block in objects) {
            fun extractField(name: String): String? {
                // Match "fieldName": "value" allowing escaped chars inside value
                val fieldPattern = Regex(""""?$name"?\s*:\s*"((?:[^"\\]|\\.)*)"""")
                return fieldPattern.find(block)?.groupValues?.get(1)
                    ?.replace("\\\"", "\"")
                    ?.replace("\\n", "\n")
            }
            val id = extractField("id") ?: continue
            val setting = extractField("setting") ?: continue
            val goal = extractField("goal") ?: continue
            val description = extractField("description") ?: ""
            val clipart = (extractField("clipart") ?: "default_conversation")
                .removeSuffix(".png")
                .removeSuffix(".webp")
            scenarios.add(ScenarioEntity(id = id, setting = setting, goal = goal, description = description, clipart = clipart))
        }
        return scenarios
    }

    private fun extractJsonObjects(text: String): List<String> {
        // Find top-level { } blocks using brace counting (handles nested braces and strings)
        val objects = mutableListOf<String>()
        var i = 0
        while (i < text.length) {
            if (text[i] == '{') {
                var depth = 0
                var inString = false
                var escaped = false
                val start = i
                while (i < text.length) {
                    val c = text[i]
                    if (escaped) {
                        escaped = false
                    } else if (c == '\\' && inString) {
                        escaped = true
                    } else if (c == '"') {
                        inString = !inString
                    } else if (!inString) {
                        if (c == '{') depth++
                        else if (c == '}') {
                            depth--
                            if (depth == 0) {
                                objects.add(text.substring(start, i + 1))
                                break
                            }
                        }
                    }
                    i++
                }
            }
            i++
        }
        return objects
    }

    fun messagesToChatHistory(messages: List<MessageEntity>): List<ChatMessage> {
        return messages.map { msg ->
            ChatMessage(
                role = if (msg.speaker == "User") "user" else "assistant",
                content = msg.content
            )
        }
    }
}
