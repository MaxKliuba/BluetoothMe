package com.android.maxclub.bluetoothme.domain.usecase.bluetooth

import com.android.maxclub.bluetoothme.domain.repository.BluetoothRepository
import javax.inject.Inject

class StartScan @Inject constructor(
    private val repository: BluetoothRepository
) {
    suspend operator fun invoke() = repository.startScan(10)
}