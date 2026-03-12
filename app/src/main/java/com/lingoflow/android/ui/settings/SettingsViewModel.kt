package com.lingoflow.android.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lingoflow.android.data.entity.SettingsEntity
import com.lingoflow.android.data.repository.SettingsRepository
import com.lingoflow.android.llm.GeminiApiClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val geminiApiClient: GeminiApiClient
) : ViewModel() {

    private val _settings = MutableStateFlow(SettingsEntity())
    val settings: StateFlow<SettingsEntity> = _settings

    val themeMode: StateFlow<String> = _settings
        .map { it.theme }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "system")

    private val _apiKey = MutableStateFlow("")
    val apiKey: StateFlow<String> = _apiKey

    init {
        viewModelScope.launch {
            settingsRepository.ensureInitialized()
            _apiKey.value = geminiApiClient.getApiKey() ?: ""
            settingsRepository.observe().collect { settings ->
                _settings.value = settings
            }
        }
    }

    fun updateTheme(theme: String) {
        viewModelScope.launch {
            settingsRepository.update(_settings.value.copy(theme = theme))
        }
    }

    fun updatePracticeLanguage(language: String) {
        viewModelScope.launch {
            settingsRepository.update(_settings.value.copy(practiceLanguage = language))
        }
    }

    fun updateUiLanguage(language: String) {
        viewModelScope.launch {
            settingsRepository.update(_settings.value.copy(uiLanguage = language))
        }
    }

    fun updateModelType(type: String) {
        viewModelScope.launch {
            settingsRepository.update(_settings.value.copy(modelType = type))
        }
    }

    fun updateCloudModel(model: String) {
        viewModelScope.launch {
            settingsRepository.update(_settings.value.copy(cloudModel = model))
            geminiApiClient.setModel(model)
        }
    }

    fun updateApiKey(key: String) {
        _apiKey.value = key
        geminiApiClient.setApiKey(key)
    }
}
