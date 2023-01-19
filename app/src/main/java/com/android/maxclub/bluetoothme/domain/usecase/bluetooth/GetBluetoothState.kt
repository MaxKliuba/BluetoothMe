package com.android.maxclub.bluetoothme.domain.usecase.bluetooth

import com.android.maxclub.bluetoothme.domain.repository.BluetoothRepository
import javax.inject.Inject

class GetBluetoothState @Inject constructor(
    private val repository: BluetoothRepository
) {
    operator fun invoke() = repository.getState()
}