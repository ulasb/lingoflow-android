package com.lingoflow.android.ui.history

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lingoflow.android.data.entity.HistoryEntity
import com.lingoflow.android.data.entity.MessageEntity
import com.lingoflow.android.data.repository.HistoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val historyRepository: HistoryRepository
) : ViewModel() {

    private val historyId: Long = savedStateHandle["historyId"] ?: 0L

    val messages: StateFlow<List<MessageEntity>> = historyRepository.observeMessages(historyId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _history = MutableStateFlow<HistoryEntity?>(null)
    val history: StateFlow<HistoryEntity?> = _history

    init {
        viewModelScope.launch {
            _history.value = historyRepository.getById(historyId)
        }
    }
}
