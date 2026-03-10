package com.lingoflow.android.llm

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject
import javax.inject.Singleton

sealed class DownloadState {
    data object Idle : DownloadState()
    data class Downloading(val progress: Float) : DownloadState()
    data object Completed : DownloadState()
    data class Failed(val error: String) : DownloadState()
}

@Singleton
class ModelDownloadManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val _state = MutableStateFlow<DownloadState>(DownloadState.Idle)
    val state: StateFlow<DownloadState> = _state

    val modelsDir = File(context.filesDir, "models")

    companion object {
        // Gemma 3n E4B — official Google model for Android on-device inference
        const val MODEL_URL = "https://huggingface.co/google/gemma-3n-E4B-it-litert-preview/resolve/main/gemma-3n-E4B-it-int4.task"
        const val MODEL_FILENAME = "gemma-3n-E4B-it-int4.task"
    }

    fun isModelDownloaded(): Boolean {
        return File(modelsDir, MODEL_FILENAME).exists()
    }

    suspend fun downloadModel(authToken: String? = null) {
        if (isModelDownloaded()) {
            _state.value = DownloadState.Completed
            return
        }
        _state.value = DownloadState.Downloading(0f)
        withContext(Dispatchers.IO) {
            try {
                modelsDir.mkdirs()
                val tempFile = File(modelsDir, "$MODEL_FILENAME.tmp")
                val targetFile = File(modelsDir, MODEL_FILENAME)

                val connection = URL(MODEL_URL).openConnection() as HttpURLConnection
                connection.connectTimeout = 30_000
                connection.readTimeout = 60_000
                connection.instanceFollowRedirects = true
                if (!authToken.isNullOrBlank()) {
                    connection.setRequestProperty("Authorization", "Bearer $authToken")
                }
                connection.connect()

                if (connection.responseCode != HttpURLConnection.HTTP_OK) {
                    val errorMessage = when (connection.responseCode) {
                        401 -> "Invalid or missing HuggingFace token."
                        403 -> "Access denied. Please accept the Gemma license at huggingface.co/google/gemma-3n-E4B-it-litert-preview, then retry."
                        else -> "HTTP ${connection.responseCode}: ${connection.responseMessage}"
                    }
                    _state.value = DownloadState.Failed(errorMessage)
                    return@withContext
                }

                val totalBytes = connection.contentLengthLong
                var downloadedBytes = 0L

                connection.inputStream.use { input ->
                    tempFile.outputStream().use { output ->
                        val buffer = ByteArray(65536)
                        var bytesRead: Int
                        while (input.read(buffer).also { bytesRead = it } != -1) {
                            output.write(buffer, 0, bytesRead)
                            downloadedBytes += bytesRead
                            if (totalBytes > 0) {
                                _state.value = DownloadState.Downloading(
                                    downloadedBytes.toFloat() / totalBytes
                                )
                            }
                        }
                    }
                }

                if (!tempFile.renameTo(targetFile)) {
                    tempFile.delete()
                    _state.value = DownloadState.Failed("Failed to save model file")
                    return@withContext
                }
                _state.value = DownloadState.Completed
            } catch (e: Exception) {
                _state.value = DownloadState.Failed(e.message ?: "Unknown error")
            }
        }
    }

    fun reset() {
        _state.value = DownloadState.Idle
    }
}
