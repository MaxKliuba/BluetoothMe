package com.android.maxclub.bluetoothme.feature_bluetooth.domain.usecases.bluetooth

import com.android.maxclub.bluetoothme.feature_bluetooth.domain.bluetooth.models.BluetoothDevice
import com.android.maxclub.bluetoothme.feature_bluetooth.domain.repositories.BluetoothRepository
import javax.inject.Inject

class Disconnect @Inject constructor(
    private val repository: BluetoothRepository
) {
    operator fun invoke(device: BluetoothDevice? = null) = repository.disconnect(device)
}