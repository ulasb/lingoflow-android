package com.lingoflow.android.ui.chat

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lingoflow.android.data.entity.MessageEntity
import com.lingoflow.android.data.entity.ScenarioEntity
import com.lingoflow.android.data.repository.ChatRepository
import com.lingoflow.android.data.repository.HistoryRepository
import com.lingoflow.android.data.repository.ScenarioRepository
import com.lingoflow.android.data.repository.SettingsRepository
import com.lingoflow.android.llm.LlmToken
import com.lingoflow.android.llm.MediaPipeLlmClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val chatRepository: ChatRepository,
    private val historyRepository: HistoryRepository,
    private val scenarioRepository: ScenarioRepository,
    private val settingsRepository: SettingsRepository,
    private val mediaPipeLlmClient: MediaPipeLlmClient
) : ViewModel() {

    val historyId: Long = savedStateHandle["historyId"] ?: 0L
    private val scenarioId: String = savedStateHandle["scenarioId"] ?: ""

    val messages: StateFlow<List<MessageEntity>> = historyRepository.observeMessages(historyId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _streamingMessage = MutableStateFlow("")
    val streamingMessage: StateFlow<String> = _streamingMessage

    private val _isThinking = MutableStateFlow(false)
    val isThinking: StateFlow<Boolean> = _isThinking

    private val _goalAchieved = MutableStateFlow(false)
    val goalAchieved: StateFlow<Boolean> = _goalAchieved

    private val _summary = MutableStateFlow<String?>(null)
    val summary: StateFlow<String?> = _summary

    private val _hint = MutableStateFlow<String?>(null)
    val hint: StateFlow<String?> = _hint

    private val _isLoadingHint = MutableStateFlow(false)
    val isLoadingHint: StateFlow<Boolean> = _isLoadingHint

    private val _scenario = MutableStateFlow<ScenarioEntity?>(null)
    val scenario: StateFlow<ScenarioEntity?> = _scenario

    init {
        viewModelScope.launch {
            _scenario.value = scenarioRepository.getById(scenarioId)

            // If no messages yet, generate the opening message
            val existingMessages = historyRepository.getMessages(historyId)
            if (existingMessages.isEmpty()) {
                generateBotResponse(isOpening = true)
            }
        }
    }

    fun sendMessage(text: String) {
        if (text.isBlank() || _isThinking.value) return
        viewModelScope.launch {
            historyRepository.appendMessage(historyId, "User", text.trim())
            _hint.value = null
            generateBotResponse(isOpening = false)
        }
    }

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private suspend fun ensureModelLoaded(modelType: String) {
        if (modelType == "on_device" && !mediaPipeLlmClient.isLoaded) {
            if (!mediaPipeLlmClient.isModelAvailable) {
                throw IllegalStateException("No on-device model downloaded. Go to Settings to download or switch to Cloud mode.")
            }
            mediaPipeLlmClient.loadModel()
        }
    }

    private suspend fun generateBotResponse(isOpening: Boolean) {
        val scenario = _scenario.value ?: return
        val settings = settingsRepository.get()

        _isThinking.value = true
        _streamingMessage.value = ""
        _error.value = null

        try {
            ensureModelLoaded(settings.modelType)
            val systemPrompt = chatRepository.buildSystemPrompt(
                practiceLanguage = settings.practiceLanguage,
                uiLanguage = settings.uiLanguage,
                setting = scenario.setting,
                goal = scenario.goal
            )

            val currentMessages = historyRepository.getMessages(historyId)
            val chatHistory = chatRepository.messagesToChatHistory(currentMessages)

            val userMessage = if (isOpening) {
                "Start the conversation by greeting me in character."
            } else {
                currentMessages.lastOrNull { it.speaker == "User" }?.content ?: return
            }

            val fullResponse = StringBuilder()
            chatRepository.streamChatTurn(
                systemPrompt = systemPrompt,
                history = if (isOpening) emptyList() else chatHistory.dropLast(1),
                userMessage = userMessage,
                modelType = settings.modelType
            ).collect { token ->
                when (token) {
                    is LlmToken.Content -> {
                        fullResponse.append(token.text)
                        _streamingMessage.value = fullResponse.toString()
                    }
                    is LlmToken.Done -> {
                        // Save the bot message
                        val botMessage = fullResponse.toString().trim()
                        if (botMessage.isNotEmpty()) {
                            historyRepository.appendMessage(historyId, "Bot", botMessage)
                        }
                        _streamingMessage.value = ""
                        _isThinking.value = false

                        // Evaluate goal (skip for opening message)
                        if (!isOpening && !_goalAchieved.value) {
                            evaluateGoal(settings.modelType, scenario.goal)
                        }
                    }
                    is LlmToken.Error -> {
                        _streamingMessage.value = ""
                        _isThinking.value = false
                        _error.value = token.cause.message ?: "LLM generation failed"
                    }
                }
            }
        } catch (e: Exception) {
            _isThinking.value = false
            _streamingMessage.value = ""
            _error.value = e.message ?: "An error occurred"
        }
    }

    private suspend fun evaluateGoal(modelType: String, goal: String) {
        val allMessages = historyRepository.getMessages(historyId)
        val reached = chatRepository.evaluateGoal(modelType, goal, allMessages)
        if (reached) {
            _goalAchieved.value = true
            val settings = settingsRepository.get()

            // Mark completed, increment score, delete scenario
            historyRepository.markCompleted(historyId)
            settingsRepository.incrementScore()
            scenarioRepository.deleteById(scenarioId)

            // Generate summary
            val summaryText = chatRepository.generateSummary(
                modelType = modelType,
                practiceLanguage = settings.practiceLanguage,
                uiLanguage = settings.uiLanguage,
                goal = goal,
                messages = allMessages
            )
            if (summaryText != null) {
                historyRepository.saveSummary(historyId, summaryText)
                _summary.value = summaryText
            }

            // Generate replacement scenario
            viewModelScope.launch {
                val newScenarios = chatRepository.generateScenarios(
                    modelType = modelType,
                    practiceLanguage = settings.practiceLanguage,
                    uiLanguage = settings.uiLanguage,
                    count = 1
                )
                for (s in newScenarios) {
                    scenarioRepository.replaceAll(
                        scenarioRepository.getAll() + s
                    )
                }
            }
        }
    }

    fun requestHint() {
        viewModelScope.launch {
            val scenario = _scenario.value ?: return@launch
            val settings = settingsRepository.get()
            val currentMessages = historyRepository.getMessages(historyId)

            _isLoadingHint.value = true
            val hintText = chatRepository.generateHint(
                modelType = settings.modelType,
                practiceLanguage = settings.practiceLanguage,
                uiLanguage = settings.uiLanguage,
                setting = scenario.setting,
                goal = scenario.goal,
                messages = currentMessages
            )
            _hint.value = hintText
            _isLoadingHint.value = false
        }
    }

    fun dismissHint() {
        _hint.value = null
    }

    private val _explanation = MutableStateFlow<String?>(null)
    val explanation: StateFlow<String?> = _explanation

    private val _isLoadingExplanation = MutableStateFlow(false)
    val isLoadingExplanation: StateFlow<Boolean> = _isLoadingExplanation

    private val _explanationSentence = MutableStateFlow<String?>(null)
    val explanationSentence: StateFlow<String?> = _explanationSentence

    fun explainLine(sentence: String) {
        _explanationSentence.value = sentence
        _explanation.value = null
        _isLoadingExplanation.value = true
        viewModelScope.launch {
            val settings = settingsRepository.get()
            try {
                ensureModelLoaded(settings.modelType)
            } catch (_: Exception) {}
            val result = chatRepository.explainLine(
                modelType = settings.modelType,
                practiceLanguage = settings.practiceLanguage,
                uiLanguage = settings.uiLanguage,
                sentence = sentence
            )
            _explanation.value = result ?: "Could not generate explanation."
            _isLoadingExplanation.value = false
        }
    }

    fun dismissExplanation() {
        _explanationSentence.value = null
        _explanation.value = null
        _isLoadingExplanation.value = false
    }
}
