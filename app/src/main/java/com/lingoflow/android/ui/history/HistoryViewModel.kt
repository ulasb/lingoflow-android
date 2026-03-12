package com.lingoflow.android.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lingoflow.android.data.entity.HistoryEntity
import com.lingoflow.android.data.repository.HistoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val historyRepository: HistoryRepository
) : ViewModel() {

    val completedConversations: StateFlow<List<HistoryEntity>> =
        historyRepository.observeCompleted()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun deleteConversation(id: Long) {
        viewModelScope.launch {
            historyRepository.deleteById(id)
        }
    }

    fun deleteAll() {
        viewModelScope.launch {
            historyRepository.deleteAllCompleted()
        }
    }
}
