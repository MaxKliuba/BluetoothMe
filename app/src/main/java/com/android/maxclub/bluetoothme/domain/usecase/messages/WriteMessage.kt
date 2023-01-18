package com.android.maxclub.bluetoothme.domain.usecase.messages

import com.android.maxclub.bluetoothme.domain.messages.Message
import com.android.maxclub.bluetoothme.domain.repository.BluetoothRepository
import javax.inject.Inject

class WriteMessage @Inject constructor(
    private val repository: BluetoothRepository
) {
    operator fun invoke(value: String) = repository.writeMessage(Message(Message.Type.Output, value))
}