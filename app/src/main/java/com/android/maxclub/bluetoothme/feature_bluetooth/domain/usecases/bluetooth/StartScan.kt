package com.android.maxclub.bluetoothme.feature_bluetooth.domain.usecases.bluetooth

import com.android.maxclub.bluetoothme.feature_bluetooth.domain.repositories.BluetoothRepository
import javax.inject.Inject

class StartScan @Inject constructor(
    private val repository: BluetoothRepository
) {
    suspend operator fun invoke() = repository.startScan(10)
}