package com.tech.maxclub.bluetoothme.feature.bluetooth.domain.usecases.bluetooth

import com.tech.maxclub.bluetoothme.feature.bluetooth.domain.repositories.BluetoothRepository
import javax.inject.Inject

class StartScan @Inject constructor(
    private val repository: BluetoothRepository
) {
    suspend operator fun invoke() = repository.startScan(10)
}