package com.android.maxclub.bluetoothme.domain.usecase

import com.android.maxclub.bluetoothme.domain.model.BondedDevice
import com.android.maxclub.bluetoothme.domain.repository.BluetoothRepository
import javax.inject.Inject

class ConnectUseCase @Inject constructor(
    private val repository: BluetoothRepository
) {
    operator fun invoke(device: BondedDevice) = repository.connect(device)
}