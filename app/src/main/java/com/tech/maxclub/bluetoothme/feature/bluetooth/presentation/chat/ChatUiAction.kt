package com.tech.maxclub.bluetoothme.feature.bluetooth.presentation.chat

sealed class ChatUiAction {
    object ScrollToBottom : ChatUiAction()
    object ShowSendingErrorMessage : ChatUiAction()
}