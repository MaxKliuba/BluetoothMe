package com.android.maxclub.bluetoothme.feature.bluetooth.domain.usecases.messages

import com.android.maxclub.bluetoothme.feature.bluetooth.data.mappers.toMessage
import com.android.maxclub.bluetoothme.feature.bluetooth.domain.messages.Message
import com.android.maxclub.bluetoothme.feature.bluetooth.domain.repositories.BluetoothRepository
import java.util.Date
import javax.inject.Inject

class WriteMessage @Inject constructor(
    private val repository: BluetoothRepository
) {
    operator fun invoke(messageValue: String) =
        repository.writeMessage(
            messageValue.toMessage(type = Message.Type.Output)
        )

    operator fun invoke(tag: String, value: String) =
        repository.writeMessage(
            Message(
                type = Message.Type.Output,
                tag = tag,
                value = value,
                timestamp = Date().time,
            )
        )
}