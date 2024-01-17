package com.tech.maxclub.bluetoothme.feature.bluetooth.domain.usecases.bluetooth

import com.tech.maxclub.bluetoothme.feature.bluetooth.domain.bluetooth.models.BluetoothDevice
import com.tech.maxclub.bluetoothme.feature.bluetooth.domain.repositories.BluetoothRepository
import javax.inject.Inject

class UpdateBluetoothDevice @Inject constructor(
    private val repository: BluetoothRepository
) {
    operator fun invoke(device: BluetoothDevice) = repository.updateBluetoothDevice(device)
}