package com.android.maxclub.bluetoothme.feature.bluetooth.domain.usecases.bluetooth

import com.android.maxclub.bluetoothme.feature.bluetooth.domain.repositories.BluetoothRepository
import javax.inject.Inject

class StartScan @Inject constructor(
    private val repository: BluetoothRepository
) {
    suspend operator fun invoke() = repository.startScan(10)
}