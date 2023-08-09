package com.android.maxclub.bluetoothme.feature.bluetooth.domain.usecases.messages

import javax.inject.Inject

data class MessagesUseCases @Inject constructor(
    val getMessages: GetMessages,
    val writeMessage: WriteMessage,
    val deleteMessages: DeleteMessages,
)
