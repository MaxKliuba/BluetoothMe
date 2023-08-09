package com.android.maxclub.bluetoothme.feature.bluetooth.domain.usecases.bluetooth

import com.android.maxclub.bluetoothme.feature.bluetooth.domain.bluetooth.models.BluetoothDevice
import com.android.maxclub.bluetoothme.feature.bluetooth.domain.repositories.BluetoothRepository
import javax.inject.Inject

class Disconnect @Inject constructor(
    private val repository: BluetoothRepository
) {
    operator fun invoke(device: BluetoothDevice? = null) = repository.disconnect(device)
}