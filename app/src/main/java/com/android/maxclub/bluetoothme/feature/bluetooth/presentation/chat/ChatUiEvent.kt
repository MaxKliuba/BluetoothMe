package com.android.maxclub.bluetoothme.feature.bluetooth.presentation.chat

import androidx.compose.ui.focus.FocusState

sealed class ChatUiEvent {
    data class OnChangeMessageValue(val messageValue: String) : ChatUiEvent()
    data class OnMessageTextFieldFocusChanged(val focusState: FocusState) : ChatUiEvent()
    data class OnSendMessage(val messageValue: String) : ChatUiEvent()
    object OnDeleteMessages : ChatUiEvent()
}