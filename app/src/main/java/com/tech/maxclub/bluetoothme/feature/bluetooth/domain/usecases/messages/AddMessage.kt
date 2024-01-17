package com.tech.maxclub.bluetoothme.feature.bluetooth.domain.usecases.messages

import com.tech.maxclub.bluetoothme.feature.bluetooth.data.mappers.toMessage
import com.tech.maxclub.bluetoothme.feature.bluetooth.domain.messages.Message
import com.tech.maxclub.bluetoothme.feature.bluetooth.domain.repositories.BluetoothRepository
import javax.inject.Inject

class AddMessage @Inject constructor(
    private val repository: BluetoothRepository
) {
    operator fun invoke(messageValue: String, messageType: Message.Type) =
        repository.addMessage(
            messageValue.toMessage(type = messageType)
        )
}