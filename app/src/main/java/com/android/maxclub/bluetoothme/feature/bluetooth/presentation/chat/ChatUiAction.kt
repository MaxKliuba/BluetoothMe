package com.android.maxclub.bluetoothme.feature.bluetooth.presentation.chat

sealed class ChatUiAction {
    object ScrollToBottom : ChatUiAction()
    object ShowSendErrorMessage : ChatUiAction()
}