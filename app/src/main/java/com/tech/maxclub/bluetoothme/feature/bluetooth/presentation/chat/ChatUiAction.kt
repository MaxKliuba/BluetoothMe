package com.tech.maxclub.bluetoothme.feature.bluetooth.presentation.chat

sealed class ChatUiAction {
    data object ScrollToBottom : ChatUiAction()
    data object ShowSendingErrorMessage : ChatUiAction()
}