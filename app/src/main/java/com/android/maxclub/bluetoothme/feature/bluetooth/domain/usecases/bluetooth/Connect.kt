package com.android.maxclub.bluetoothme.feature.bluetooth.domain.usecases.bluetooth

import com.android.maxclub.bluetoothme.feature.bluetooth.domain.bluetooth.models.BluetoothDevice
import com.android.maxclub.bluetoothme.feature.bluetooth.domain.repositories.BluetoothRepository
import javax.inject.Inject

class Connect @Inject constructor(
    private val repository: BluetoothRepository
) {
    suspend operator fun invoke(device: BluetoothDevice) = repository.connect(device)
}