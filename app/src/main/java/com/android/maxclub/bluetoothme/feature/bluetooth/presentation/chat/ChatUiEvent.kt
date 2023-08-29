package com.android.maxclub.bluetoothme.feature.bluetooth.presentation.chat

import androidx.compose.ui.text.input.TextFieldValue

sealed class ChatUiEvent {
    object OnDeleteMessages : ChatUiEvent()
    data class OnChangeMessageValue(val messageValue: TextFieldValue) : ChatUiEvent()
    data class OnSendMessage(val messageValue: String) : ChatUiEvent()
}