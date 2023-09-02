package com.android.maxclub.bluetoothme.feature.bluetooth.presentation.chat

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.maxclub.bluetoothme.core.exceptions.WriteMessageException
import com.android.maxclub.bluetoothme.core.util.sendIn
import com.android.maxclub.bluetoothme.core.util.update
import com.android.maxclub.bluetoothme.feature.bluetooth.domain.messages.MessageValueValidator
import com.android.maxclub.bluetoothme.feature.bluetooth.domain.usecases.messages.MessagesUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val messagesUseCases: MessagesUseCases,
    private val messageValueValidator: MessageValueValidator,
) : ViewModel() {

    private val _uiState = mutableStateOf(
        ChatUiState(
            messages = emptyList(),
            messageValue = TextFieldValue(""),
        )
    )
    val uiState: State<ChatUiState> = _uiState

    private val uiActionChannel = Channel<ChatUiAction>()
    val uiAction = uiActionChannel.receiveAsFlow()

    private var getMessagesJob: Job? = null

    init {
        getMessages()
    }

    fun deleteMessages() {
        messagesUseCases.deleteMessages()
    }

    fun tryChangeMessageValue(newMessageValue: String) {
        tryChangeMessageValue(TextFieldValue(newMessageValue, TextRange(newMessageValue.length)))
    }

    fun tryChangeMessageValue(newMessageValue: TextFieldValue) {
        if (messageValueValidator(newMessageValue.text)) {
            _uiState.update { it.copy(messageValue = newMessageValue) }
        }
    }

    fun trySendMessage(messageValue: String) {
        if (messageValue.isNotEmpty() && messageValueValidator(messageValue)) {
            writeMessage(messageValue)
            tryChangeMessageValue("")
        }
    }

    private fun getMessages() {
        getMessagesJob?.cancel()
        getMessagesJob = messagesUseCases.getMessages()
            .onEach { messages ->
                _uiState.update { it.copy(messages = messages) }
                uiActionChannel.sendIn(ChatUiAction.ScrollToBottom, viewModelScope)
            }
            .catch { it.printStackTrace() }
            .launchIn(viewModelScope)
    }

    private fun writeMessage(messageValue: String) {
        try {
            messagesUseCases.writeMessage(messageValue)
        } catch (e: WriteMessageException) {
            e.printStackTrace()

            uiActionChannel.sendIn(ChatUiAction.ShowSendingErrorMessage, viewModelScope)
        }
    }
}