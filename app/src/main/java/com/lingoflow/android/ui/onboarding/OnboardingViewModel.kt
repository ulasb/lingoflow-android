package com.lingoflow.android.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lingoflow.android.data.repository.SettingsRepository
import com.lingoflow.android.llm.GeminiApiClient
import com.lingoflow.android.llm.MediaPipeLlmClient
import com.lingoflow.android.llm.ModelDownloadManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val mediaPipeLlmClient: MediaPipeLlmClient,
    private val geminiApiClient: GeminiApiClient,
    private val modelDownloadManager: ModelDownloadManager
) : ViewModel() {

    private val _shouldShowOnboarding = MutableStateFlow(true)
    val shouldShowOnboarding: StateFlow<Boolean> = _shouldShowOnboarding

    init {
        viewModelScope.launch {
            settingsRepository.ensureInitialized()
            // Skip onboarding if model is available or cloud is configured
            val hasModel = mediaPipeLlmClient.isModelAvailable
            val hasCloud = geminiApiClient.hasApiKey()
            _shouldShowOnboarding.value = !hasModel && !hasCloud
        }
    }

    fun setupCloud(apiKey: String) {
        geminiApiClient.setApiKey(apiKey)
        viewModelScope.launch {
            settingsRepository.update(
                settingsRepository.get().copy(modelType = "cloud")
            )
        }
    }

    fun validateAndSetupCloud(apiKey: String, onResult: (success: Boolean, error: String?) -> Unit) {
        geminiApiClient.setApiKey(apiKey)
        viewModelScope.launch {
            try {
                // Make a minimal API call to validate the key
                geminiApiClient.generateBlocking(
                    systemPrompt = "",
                    userMessage = "Say hi"
                )
                settingsRepository.update(
                    settingsRepository.get().copy(modelType = "cloud")
                )
                onResult(true, null)
            } catch (e: Exception) {
                onResult(false, e.message ?: "Failed to validate API key")
            }
        }
    }

    fun setupOnDevice() {
        viewModelScope.launch {
            settingsRepository.update(
                settingsRepository.get().copy(modelType = "on_device")
            )
        }
    }
}
