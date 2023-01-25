package com.android.maxclub.bluetoothme.domain.usecase.messages

import com.android.maxclub.bluetoothme.domain.messages.Message
import com.android.maxclub.bluetoothme.domain.repository.BluetoothRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetMessages @Inject constructor(
    private val repository: BluetoothRepository
) {
    operator fun invoke(): Flow<List<Message>> = repository.getMessages()
        .map { it.reversed() }
}