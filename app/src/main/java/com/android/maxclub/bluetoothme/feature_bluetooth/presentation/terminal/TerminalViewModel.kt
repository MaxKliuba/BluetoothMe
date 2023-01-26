package com.android.maxclub.bluetoothme.feature_bluetooth.presentation.terminal

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.maxclub.bluetoothme.feature_bluetooth.domain.messages.Message
import com.android.maxclub.bluetoothme.feature_bluetooth.domain.usecases.messages.MessagesUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class TerminalViewModel @Inject constructor(
    private val messagesUseCases: MessagesUseCases
) : ViewModel() {
    private val _uiState = mutableStateOf(emptyList<Message>())
    val uiState: State<List<Message>> = _uiState

    private var getMessagesJob: Job? = null

    init {
        getMessages()
    }

    private fun getMessages() {
        getMessagesJob?.cancel()
        getMessagesJob = messagesUseCases.getMessages()
            .onEach { messages ->
                _uiState.value = messages
            }
            .catch { it.printStackTrace() }
            .launchIn(viewModelScope)
    }
}