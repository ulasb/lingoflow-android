package com.lingoflow.android.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lingoflow.android.llm.DownloadState
import com.lingoflow.android.llm.MediaPipeLlmClient
import com.lingoflow.android.llm.ModelDownloadManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ModelDownloadViewModel @Inject constructor(
    private val modelDownloadManager: ModelDownloadManager,
    private val mediaPipeLlmClient: MediaPipeLlmClient
) : ViewModel() {

    val downloadState: StateFlow<DownloadState> = modelDownloadManager.state

    fun startDownload(hfToken: String? = null) {
        viewModelScope.launch {
            modelDownloadManager.downloadModel(authToken = hfToken?.takeIf { it.isNotBlank() })
            // Eagerly load the model into memory after download
            if (modelDownloadManager.isModelDownloaded()) {
                mediaPipeLlmClient.loadModel()
            }
        }
    }

    fun retryDownload(hfToken: String? = null) {
        modelDownloadManager.reset()
        startDownload(hfToken)
    }
}
