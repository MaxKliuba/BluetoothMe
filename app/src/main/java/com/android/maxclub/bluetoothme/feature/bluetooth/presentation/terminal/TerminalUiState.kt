package com.android.maxclub.bluetoothme.feature.bluetooth.presentation.terminal

import com.android.maxclub.bluetoothme.feature.bluetooth.domain.messages.Message

data class TerminalUiState(
    val messages: List<Message>,
    val messageValue: String,
    val isHintVisible: Boolean,
)