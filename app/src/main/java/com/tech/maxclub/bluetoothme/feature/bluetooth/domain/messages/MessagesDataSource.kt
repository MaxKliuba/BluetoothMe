package com.tech.maxclub.bluetoothme.feature.bluetooth.domain.messages

import kotlinx.coroutines.flow.Flow

interface MessagesDataSource {

    fun getMessages(): Flow<List<Message>>

    fun addMessage(message: Message)

    fun deleteMessages()
}