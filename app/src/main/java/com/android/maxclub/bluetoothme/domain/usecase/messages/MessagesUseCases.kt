package com.android.maxclub.bluetoothme.domain.usecase.messages

import javax.inject.Inject

data class MessagesUseCases @Inject constructor(
    val writeMessage: WriteMessage,
    val getMessages: GetMessages,
)
