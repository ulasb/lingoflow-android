package com.lingoflow.android.llm

import android.content.Context
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

// Gemini API data classes
data class GeminiRequest(
    val contents: List<GeminiContent>,
    @SerializedName("system_instruction") val systemInstruction: GeminiContent? = null
)

data class GeminiContent(
    val role: String? = null,
    val parts: List<GeminiPart>
)

data class GeminiPart(val text: String)

data class GeminiResponse(
    val candidates: List<GeminiCandidate>?
)

data class GeminiCandidate(
    val content: GeminiContent?
)

@Singleton
class GeminiApiClient @Inject constructor(
    @ApplicationContext private val context: Context
) : LlmClient {

    companion object {
        private const val TAG = "GeminiApiClient"
        private const val PREFS_NAME = "lingoflow_secure_prefs"
        private const val KEY_API_KEY = "gemini_api_key"
        private const val KEY_MODEL = "gemini_model"
        val AVAILABLE_MODELS = listOf("gemini-2.5-flash", "gemini-2.0-flash", "gemini-2.5-pro")
    }

    private val gson = Gson()
    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(120, TimeUnit.SECONDS)
        .build()

    private fun getEncryptedPrefs() = EncryptedSharedPreferences.create(
        context,
        PREFS_NAME,
        MasterKey.Builder(context).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build(),
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun getApiKey(): String? = getEncryptedPrefs().getString(KEY_API_KEY, null)

    fun setApiKey(key: String) {
        getEncryptedPrefs().edit().putString(KEY_API_KEY, key).apply()
    }

    fun getModel(): String = getEncryptedPrefs().getString(KEY_MODEL, AVAILABLE_MODELS[0])
        ?: AVAILABLE_MODELS[0]

    fun setModel(model: String) {
        getEncryptedPrefs().edit().putString(KEY_MODEL, model).apply()
    }

    fun hasApiKey(): Boolean = !getApiKey().isNullOrBlank()

    private fun buildRequest(
        systemPrompt: String,
        history: List<ChatMessage>,
        userMessage: String,
        model: String,
        apiKey: String
    ): Request {
        val contents = mutableListOf<GeminiContent>()
        for (msg in history) {
            val role = if (msg.role == "user") "user" else "model"
            contents.add(GeminiContent(role = role, parts = listOf(GeminiPart(msg.content))))
        }
        contents.add(GeminiContent(role = "user", parts = listOf(GeminiPart(userMessage))))

        val request = GeminiRequest(
            contents = contents,
            systemInstruction = GeminiContent(parts = listOf(GeminiPart(systemPrompt)))
        )

        val url = "https://generativelanguage.googleapis.com/v1beta/models/$model:generateContent?key=$apiKey"
        val body = gson.toJson(request).toRequestBody("application/json".toMediaType())
        return Request.Builder().url(url).post(body).build()
    }

    override fun generate(
        systemPrompt: String,
        history: List<ChatMessage>,
        userMessage: String
    ): Flow<LlmToken> = flow {
        try {
            val response = callApi(systemPrompt, history, userMessage)
            emit(LlmToken.Content(response))
            emit(LlmToken.Done)
        } catch (e: Exception) {
            Log.e(TAG, "Generation failed", e)
            emit(LlmToken.Error(e))
        }
    }

    override suspend fun generateBlocking(
        systemPrompt: String,
        history: List<ChatMessage>,
        userMessage: String
    ): String = callApi(systemPrompt, history, userMessage)

    private suspend fun callApi(
        systemPrompt: String,
        history: List<ChatMessage>,
        userMessage: String
    ): String = withContext(Dispatchers.IO) {
        val apiKey = getApiKey() ?: throw IllegalStateException("No API key configured")
        val model = getModel()
        val request = buildRequest(systemPrompt, history, userMessage, model, apiKey)
        val response = client.newCall(request).execute()

        if (!response.isSuccessful) {
            val errorBody = response.body?.string() ?: ""
            Log.e(TAG, "API error ${response.code}: $errorBody")
            if (response.code == 429) {
                val retryAfter = response.header("Retry-After")
                Log.e(TAG, "Rate limit headers — Retry-After: $retryAfter")
            }
            val userMessage = when (response.code) {
                400 -> "Invalid request. Please check your API key and try again."
                401, 403 -> "Invalid API key. Please check your Gemini API key in Settings."
                404 -> "Model \"$model\" not found. Try selecting a different model in Settings."
                429 -> "Free tier rate limit reached. The Gemini free plan has very low limits. Please wait a minute, or try a different model in Settings."
                500, 503 -> "Gemini service is temporarily unavailable. Please try again later."
                else -> "Gemini API error (${response.code}). Please try again."
            }
            throw RuntimeException(userMessage)
        }

        val body = response.body?.string() ?: throw RuntimeException("Empty response")
        val geminiResponse = gson.fromJson(body, GeminiResponse::class.java)
        geminiResponse.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
            ?: throw RuntimeException("No content in response")
    }
}
