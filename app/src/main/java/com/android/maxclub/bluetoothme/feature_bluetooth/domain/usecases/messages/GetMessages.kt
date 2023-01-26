package com.android.maxclub.bluetoothme.feature_bluetooth.domain.usecases.messages

import com.android.maxclub.bluetoothme.feature_bluetooth.domain.messages.Message
import com.android.maxclub.bluetoothme.feature_bluetooth.domain.repositories.BluetoothRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetMessages @Inject constructor(
    private val repository: BluetoothRepository
) {
    operator fun invoke(): Flow<List<Message>> = repository.getMessages()
        .map { it.reversed() }
}