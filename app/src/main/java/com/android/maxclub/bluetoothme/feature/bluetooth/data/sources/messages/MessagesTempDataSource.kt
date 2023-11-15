package com.android.maxclub.bluetoothme.feature.bluetooth.data.sources.messages

import com.android.maxclub.bluetoothme.feature.bluetooth.domain.messages.Message
import com.android.maxclub.bluetoothme.feature.bluetooth.domain.messages.MessagesDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MessagesTempDataSource @Inject constructor() : MessagesDataSource {

    private val messages: MutableStateFlow<List<Message>> = MutableStateFlow(emptyList())

    override fun getMessages(): Flow<List<Message>> = messages

    override fun addMessage(message: Message) {
        messages.value += message
    }

    override fun deleteMessages() {
        messages.value = emptyList()
    }
}