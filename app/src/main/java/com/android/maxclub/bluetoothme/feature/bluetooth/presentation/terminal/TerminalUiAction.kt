package com.android.maxclub.bluetoothme.feature.bluetooth.presentation.terminal

sealed class TerminalUiAction {
    object ScrollToBottom : TerminalUiAction()
    object ShowSendErrorMessage : TerminalUiAction()
}