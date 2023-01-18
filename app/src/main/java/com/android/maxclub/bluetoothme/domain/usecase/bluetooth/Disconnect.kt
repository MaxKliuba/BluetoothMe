package com.android.maxclub.bluetoothme.domain.usecase.bluetooth

import com.android.maxclub.bluetoothme.domain.bluetooth.model.BluetoothDevice
import com.android.maxclub.bluetoothme.domain.repository.BluetoothRepository
import javax.inject.Inject

class Disconnect @Inject constructor(
    private val repository: BluetoothRepository
) {
    operator fun invoke(device: BluetoothDevice) = repository.disconnect(device)
}