package com.android.maxclub.bluetoothme.feature.bluetooth.presentation.terminal

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.maxclub.bluetoothme.core.exceptions.WriteMessageException
import com.android.maxclub.bluetoothme.feature.bluetooth.domain.messages.MessageValueValidator
import com.android.maxclub.bluetoothme.feature.bluetooth.domain.usecases.messages.MessagesUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TerminalViewModel @Inject constructor(
    private val messagesUseCases: MessagesUseCases,
    private val messageValueValidator: MessageValueValidator,
) : ViewModel() {
    private val _uiState = mutableStateOf(
        TerminalUiState(
            messages = emptyList(),
            messageValue = "",
            isHintVisible = true,
        )
    )
    val uiState: State<TerminalUiState> = _uiState

    private val uiActionChannel = Channel<TerminalUiAction>()
    val uiAction = uiActionChannel.receiveAsFlow()

    private var getMessagesJob: Job? = null

    init {
        getMessages()
    }

    fun onEvent(event: TerminalUiEvent) {
        when (event) {
            is TerminalUiEvent.OnChangeMessageValue -> {
                if (messageValueValidator(event.messageValue)) {
                    _uiState.value = uiState.value.copy(messageValue = event.messageValue)
                }
            }

            is TerminalUiEvent.OnFocusChange -> {
                _uiState.value = uiState.value.copy(isHintVisible = !event.focusState.isFocused)
            }

            is TerminalUiEvent.OnSendMessage -> {
                if (event.messageValue.isNotEmpty() && messageValueValidator(event.messageValue)) {
                    writeMessage(event.messageValue)
                    _uiState.value = uiState.value.copy(messageValue = "")
                }
            }

            is TerminalUiEvent.OnDeleteMessages -> {
                deleteMessages()
            }
        }
    }

    private fun getMessages() {
        getMessagesJob?.cancel()
        getMessagesJob = messagesUseCases.getMessages()
            .onEach { messages ->
                _uiState.value = uiState.value.copy(messages = messages)
                uiActionChannel.send(TerminalUiAction.ScrollToBottom)
            }
            .catch { it.printStackTrace() }
            .launchIn(viewModelScope)
    }

    private fun writeMessage(messageValue: String) {
        try {
            messagesUseCases.writeMessage(messageValue)
        } catch (e: WriteMessageException) {
            e.printStackTrace()
            viewModelScope.launch {
                uiActionChannel.send(TerminalUiAction.ShowSendErrorMessage)
            }
        }
    }

    private fun deleteMessages() {
        messagesUseCases.deleteMessages()
    }
}