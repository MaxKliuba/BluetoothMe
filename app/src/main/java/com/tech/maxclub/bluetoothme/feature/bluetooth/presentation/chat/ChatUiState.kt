package com.tech.maxclub.bluetoothme.feature.bluetooth.presentation.chat

import androidx.compose.ui.text.input.TextFieldValue
import com.tech.maxclub.bluetoothme.feature.bluetooth.domain.messages.Message

data class ChatUiState(
    val messages: List<Message>,
    val messageValue: TextFieldValue,
)