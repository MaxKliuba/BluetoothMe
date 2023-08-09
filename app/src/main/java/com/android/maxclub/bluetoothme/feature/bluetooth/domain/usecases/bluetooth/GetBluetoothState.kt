package com.android.maxclub.bluetoothme.feature.bluetooth.domain.usecases.bluetooth

import com.android.maxclub.bluetoothme.feature.bluetooth.domain.repositories.BluetoothRepository
import javax.inject.Inject

class GetBluetoothState @Inject constructor(
    private val repository: BluetoothRepository
) {
    operator fun invoke() = repository.getState()
}