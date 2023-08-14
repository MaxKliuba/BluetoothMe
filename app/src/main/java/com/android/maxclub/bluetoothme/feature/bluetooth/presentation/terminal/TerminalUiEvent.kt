package com.android.maxclub.bluetoothme.feature.bluetooth.presentation.terminal

import androidx.compose.ui.focus.FocusState

sealed class TerminalUiEvent {
    data class OnChangeMessageValue(val messageValue: String) : TerminalUiEvent()
    data class OnFocusChange(val focusState: FocusState) : TerminalUiEvent()
    data class OnSendMessage(val messageValue: String) : TerminalUiEvent()
    object OnDeleteMessages : TerminalUiEvent()
}