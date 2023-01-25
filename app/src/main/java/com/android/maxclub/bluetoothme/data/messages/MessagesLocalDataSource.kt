package com.android.maxclub.bluetoothme.data.messages

import com.android.maxclub.bluetoothme.domain.messages.Message
import com.android.maxclub.bluetoothme.domain.messages.MessagesDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class MessagesLocalDataSource : MessagesDataSource {

    private val messages: MutableStateFlow<List<Message>> = MutableStateFlow(emptyList())

    override fun addMessage(message: Message) {
        messages.value += message
    }

    override fun deleteMessage(message: Message) {
        messages.value -= message
    }

    override fun deleteAllMessages() {
        messages.value = emptyList()
    }

    override fun getMessages(): Flow<List<Message>> = messages
}