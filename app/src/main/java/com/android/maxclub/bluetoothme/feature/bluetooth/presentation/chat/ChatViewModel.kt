package com.android.maxclub.bluetoothme.feature.bluetooth.presentation.chat

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.focus.FocusState
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
class ChatViewModel @Inject constructor(
    private val messagesUseCases: MessagesUseCases,
    private val messageValueValidator: MessageValueValidator,
) : ViewModel() {
    private val _uiState = mutableStateOf(
        ChatUiState(
            messages = emptyList(),
            messageValue = "",
            isHintVisible = true,
        )
    )
    val uiState: State<ChatUiState> = _uiState

    private val uiActionChannel = Channel<ChatUiAction>()
    val uiAction = uiActionChannel.receiveAsFlow()

    private var getMessagesJob: Job? = null

    init {
        getMessages()
    }

    fun onEvent(event: ChatUiEvent) {
        when (event) {
            is ChatUiEvent.OnChangeMessageValue -> {
                tryChangeMessageValue(event.messageValue)
            }

            is ChatUiEvent.OnMessageTextFieldFocusChanged -> {
                onMessageTextFieldFocusChanged(event.focusState)
            }

            is ChatUiEvent.OnSendMessage -> {
                trySendMessage(event.messageValue)
            }

            is ChatUiEvent.OnDeleteMessages -> {
                deleteMessages()
            }
        }
    }

    private fun tryChangeMessageValue(newMessageValue: String) {
        if (messageValueValidator(newMessageValue)) {
            _uiState.value = uiState.value.copy(messageValue = newMessageValue)
        }
    }

    private fun onMessageTextFieldFocusChanged(focusState: FocusState) {
        _uiState.value = uiState.value.copy(isHintVisible = !focusState.isFocused)
    }

    private fun trySendMessage(messageValue: String) {
        if (messageValue.isNotEmpty() && messageValueValidator(messageValue)) {
            writeMessage(messageValue)
            _uiState.value = uiState.value.copy(messageValue = "")
        }
    }

    private fun deleteMessages() {
        messagesUseCases.deleteMessages()
    }

    private fun getMessages() {
        getMessagesJob?.cancel()
        getMessagesJob = messagesUseCases.getMessages()
            .onEach { messages ->
                _uiState.value = uiState.value.copy(messages = messages)
                uiActionChannel.send(ChatUiAction.ScrollToBottom)
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
                uiActionChannel.send(ChatUiAction.ShowSendErrorMessage)
            }
        }
    }
}