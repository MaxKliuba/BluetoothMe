package com.tech.maxclub.bluetoothme.feature.bluetooth.domain.usecases.bluetooth

import com.tech.maxclub.bluetoothme.feature.bluetooth.domain.repositories.BluetoothRepository
import javax.inject.Inject

class StopScan @Inject constructor(
    private val repository: BluetoothRepository
) {
    operator fun invoke() = repository.stopScan()
}