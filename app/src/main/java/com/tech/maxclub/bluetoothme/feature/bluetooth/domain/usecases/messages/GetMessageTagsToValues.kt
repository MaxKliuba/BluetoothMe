package com.tech.maxclub.bluetoothme.feature.bluetooth.domain.usecases.messages

import com.tech.maxclub.bluetoothme.feature.bluetooth.domain.messages.Message
import com.tech.maxclub.bluetoothme.feature.bluetooth.domain.repositories.BluetoothRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetMessageTagsToValues @Inject constructor(
    private val repository: BluetoothRepository
) {
    operator fun invoke(): Flow<Map<String, String>> = repository.getMessages()
        .map { messages ->
            messages
                .filter { it.type is Message.Type.Input || it.type is Message.Type.Output }
                .groupBy { it.tag }
                .mapValues { entry -> entry.value.maxByOrNull { it.timestamp }?.value ?: "" }
        }
}