package com.android.maxclub.bluetoothme.feature_bluetooth.domain.messages

import kotlinx.coroutines.flow.Flow

interface MessagesDataSource {

    fun addMessage(message: Message)

    fun deleteMessage(message: Message)

    fun deleteAllMessages()

    fun getMessages(): Flow<List<Message>>
}