package com.lingoflow.android.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lingoflow.android.data.entity.ScenarioEntity
import com.lingoflow.android.data.entity.SettingsEntity
import com.lingoflow.android.data.repository.ChatRepository
import com.lingoflow.android.data.repository.HistoryRepository
import com.lingoflow.android.data.repository.ScenarioRepository
import com.lingoflow.android.data.repository.SettingsRepository
import com.lingoflow.android.llm.MediaPipeLlmClient
import com.lingoflow.android.llm.ModelLoadState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val scenarioRepository: ScenarioRepository,
    private val settingsRepository: SettingsRepository,
    private val historyRepository: HistoryRepository,
    private val chatRepository: ChatRepository,
    private val mediaPipeLlmClient: MediaPipeLlmClient
) : ViewModel() {

    val scenarios: StateFlow<List<ScenarioEntity>> = scenarioRepository.observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _settings = MutableStateFlow(SettingsEntity())
    val settings: StateFlow<SettingsEntity> = _settings

    private val _isRegenerating = MutableStateFlow(false)
    val isRegenerating: StateFlow<Boolean> = _isRegenerating

    private val _generationError = MutableStateFlow<String?>(null)
    val generationError: StateFlow<String?> = _generationError

    val modelLoadState: StateFlow<ModelLoadState> = mediaPipeLlmClient.loadState

    init {
        viewModelScope.launch {
            settingsRepository.ensureInitialized()
            _settings.value = settingsRepository.get()

            // Auto-generate scenarios if none exist
            val existingScenarios = scenarioRepository.observeAll().first()
            if (existingScenarios.isEmpty()) {
                generateScenariosInternal()
            }

            // Keep observing settings changes
            settingsRepository.observe().collect { _settings.value = it }
        }
    }

    fun regenerateScenarios() {
        viewModelScope.launch {
            generateScenariosInternal()
        }
    }

    private suspend fun generateScenariosInternal() {
        _isRegenerating.value = true
        _generationError.value = null
        try {
            val settings = settingsRepository.get()

            // Wait for model to be ready if using on-device
            if (settings.modelType == "on_device") {
                if (!mediaPipeLlmClient.isModelAvailable) {
                    _generationError.value = "No on-device model downloaded. Go to Settings to download or switch to Cloud mode."
                    return
                }
                if (!mediaPipeLlmClient.isLoaded) {
                    mediaPipeLlmClient.loadModel()
                }
            }

            val scenarios = chatRepository.generateScenarios(
                modelType = settings.modelType,
                practiceLanguage = settings.practiceLanguage,
                uiLanguage = settings.uiLanguage
            )
            scenarioRepository.replaceAll(scenarios)
        } catch (e: Exception) {
            _generationError.value = e.message ?: "Failed to generate scenarios"
        } finally {
            _isRegenerating.value = false
        }
    }

    fun dismissError() {
        _generationError.value = null
    }

    suspend fun startChat(scenario: ScenarioEntity): Pair<Long, String> {
        val settings = settingsRepository.get()
        val historyId = historyRepository.getOrCreateForScenario(
            scenarioId = scenario.id,
            practiceLanguage = settings.practiceLanguage,
            model = if (settings.modelType == "cloud") settings.cloudModel else "on-device"
        )
        return historyId to scenario.id
    }
}
