package com.lingoflow.android.llm

import android.content.Context
import android.os.Build
import android.util.Log
import com.google.mediapipe.tasks.genai.llminference.LlmInference
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

sealed class ModelLoadState {
    data object NotAvailable : ModelLoadState()
    data object Idle : ModelLoadState()
    data object Loading : ModelLoadState()
    data object Ready : ModelLoadState()
    data class Failed(val error: String) : ModelLoadState()
}

@Singleton
class MediaPipeLlmClient @Inject constructor(
    @ApplicationContext private val context: Context
) : LlmClient {

    companion object {
        private const val TAG = "MediaPipeLlmClient"
    }

    private var inference: LlmInference? = null
    private val mutex = Mutex()

    private val _loadState = MutableStateFlow<ModelLoadState>(ModelLoadState.Idle)
    val loadState: StateFlow<ModelLoadState> = _loadState

    val isModelAvailable: Boolean
        get() = getModelFile() != null

    val isLoaded: Boolean
        get() = inference != null

    fun getModelFile(): File? {
        val dir = File(context.filesDir, "models")
        if (!dir.exists()) return null
        return dir.listFiles()?.firstOrNull {
            it.extension == "bin" || it.extension == "task"
        }
    }

    suspend fun loadModel() {
        if (inference != null) {
            _loadState.value = ModelLoadState.Ready
            return
        }
        // Use NonCancellable so navigation/ViewModel destruction doesn't abort
        // model loading — this is a singleton that outlives any single screen.
        withContext(NonCancellable) {
            mutex.withLock {
                if (inference != null) {
                    _loadState.value = ModelLoadState.Ready
                    return@withLock
                }
                val modelFile = getModelFile()
                if (modelFile == null) {
                    _loadState.value = ModelLoadState.NotAvailable
                    return@withLock
                }
                _loadState.value = ModelLoadState.Loading
                try {
                    val backend = if (isEmulator()) {
                        Log.i(TAG, "Emulator detected, using CPU backend")
                        LlmInference.Backend.CPU
                    } else {
                        Log.i(TAG, "Physical device detected, using GPU backend")
                        LlmInference.Backend.GPU
                    }
                    val options = LlmInference.LlmInferenceOptions.builder()
                        .setModelPath(modelFile.absolutePath)
                        .setMaxTokens(4096)
                        .setMaxTopK(64)
                        .setPreferredBackend(backend)
                        .build()
                    inference = withContext(Dispatchers.IO) {
                        LlmInference.createFromOptions(context, options)
                    }
                    _loadState.value = ModelLoadState.Ready
                    Log.i(TAG, "Model loaded successfully from ${modelFile.name} (${backend.name})")
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to load model", e)
                    _loadState.value = ModelLoadState.Failed(e.message ?: "Unknown error")
                }
            }
        }
    }

    private fun isEmulator(): Boolean {
        return Build.FINGERPRINT.startsWith("generic") ||
                Build.FINGERPRINT.startsWith("unknown") ||
                Build.MODEL.contains("google_sdk") ||
                Build.MODEL.contains("Emulator") ||
                Build.MODEL.contains("Android SDK built for x86") ||
                Build.MANUFACTURER.contains("Genymotion") ||
                Build.HARDWARE.contains("goldfish") ||
                Build.HARDWARE.contains("ranchu") ||
                Build.PRODUCT.contains("sdk") ||
                Build.PRODUCT.contains("emulator")
    }

    private fun formatPrompt(systemPrompt: String, history: List<ChatMessage>, userMessage: String): String {
        val sb = StringBuilder()
        sb.appendLine("<start_of_turn>user")
        sb.appendLine(systemPrompt)
        sb.appendLine("<end_of_turn>")
        for (msg in history) {
            val role = if (msg.role == "user") "user" else "model"
            sb.appendLine("<start_of_turn>$role")
            sb.appendLine(msg.content)
            sb.appendLine("<end_of_turn>")
        }
        sb.appendLine("<start_of_turn>user")
        sb.appendLine(userMessage)
        sb.appendLine("<end_of_turn>")
        sb.appendLine("<start_of_turn>model")
        return sb.toString()
    }

    override fun generate(
        systemPrompt: String,
        history: List<ChatMessage>,
        userMessage: String
    ): Flow<LlmToken> = callbackFlow {
        val llm = inference ?: run {
            trySend(LlmToken.Error(IllegalStateException("Model not loaded")))
            close()
            return@callbackFlow
        }
        val prompt = formatPrompt(systemPrompt, history, userMessage)
        try {
            val response = withContext(Dispatchers.IO) {
                llm.generateResponse(prompt)
            }
            trySend(LlmToken.Content(response))
            trySend(LlmToken.Done)
        } catch (e: Exception) {
            trySend(LlmToken.Error(e))
        }
        close()
        awaitClose()
    }

    override suspend fun generateBlocking(
        systemPrompt: String,
        history: List<ChatMessage>,
        userMessage: String
    ): String {
        val llm = inference ?: throw IllegalStateException("Model not loaded")
        val prompt = formatPrompt(systemPrompt, history, userMessage)
        return withContext(Dispatchers.IO) {
            llm.generateResponse(prompt)
        }
    }
}
