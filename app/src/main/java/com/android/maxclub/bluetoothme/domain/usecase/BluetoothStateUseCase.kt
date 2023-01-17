package com.android.maxclub.bluetoothme.domain.usecase

import com.android.maxclub.bluetoothme.domain.repository.BluetoothRepository
import javax.inject.Inject

class BluetoothStateUseCase @Inject constructor(
    private val repository: BluetoothRepository
) {
    operator fun invoke() = repository.getState()
}