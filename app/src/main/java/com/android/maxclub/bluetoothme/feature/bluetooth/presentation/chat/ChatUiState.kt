package com.android.maxclub.bluetoothme.feature.bluetooth.presentation.chat

import com.android.maxclub.bluetoothme.feature.bluetooth.domain.messages.Message

data class ChatUiState(
    val messages: List<Message>,
    val messageValue: String,
    val isHintVisible: Boolean,
)