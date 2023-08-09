package com.android.maxclub.bluetoothme.feature.bluetooth.domain.usecases.messages

import com.android.maxclub.bluetoothme.feature.bluetooth.domain.messages.Message
import com.android.maxclub.bluetoothme.feature.bluetooth.domain.repositories.BluetoothRepository
import javax.inject.Inject

class WriteMessage @Inject constructor(
    private val repository: BluetoothRepository
) {
    operator fun invoke(value: String) = repository.writeMessage(Message(Message.Type.Output, value))
}