package com.android.maxclub.bluetoothme.feature.bluetooth.presentation.chat

sealed class ChatUiEvent {
    object OnDeleteMessages : ChatUiEvent()
    data class OnChangeMessageValue(val messageValue: String) : ChatUiEvent()
    data class OnSendMessage(val messageValue: String) : ChatUiEvent()
}