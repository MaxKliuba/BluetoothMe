package com.tech.maxclub.bluetoothme.feature.bluetooth.domain.usecases.messages

import com.tech.maxclub.bluetoothme.feature.bluetooth.domain.repositories.BluetoothRepository
import javax.inject.Inject

class DeleteMessages @Inject constructor(
    private val repository: BluetoothRepository
) {
    operator fun invoke() = repository.deleteMessages()
}