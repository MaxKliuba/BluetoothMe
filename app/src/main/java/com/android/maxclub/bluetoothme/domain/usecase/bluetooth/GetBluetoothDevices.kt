package com.android.maxclub.bluetoothme.domain.usecase.bluetooth

import com.android.maxclub.bluetoothme.domain.bluetooth.model.BluetoothDevice
import com.android.maxclub.bluetoothme.domain.bluetooth.model.BluetoothDeviceState
import com.android.maxclub.bluetoothme.domain.bluetooth.model.DeviceType
import com.android.maxclub.bluetoothme.domain.repository.BluetoothRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetBluetoothDevices @Inject constructor(
    private val repository: BluetoothRepository
) {
    private val sortedDeviceTypes = listOf(
        DeviceType.Classic::class,
        DeviceType.Ble::class,
        DeviceType.Dual::class,
        DeviceType.Unknown::class,
    )

    operator fun invoke(): Flow<List<BluetoothDevice>> = repository.getBluetoothDevices()
        .map { bondedDevices ->
            bondedDevices.sortedWith(
                compareBy<BluetoothDevice> { it.state !is BluetoothDeviceState.Connected }
                    .thenBy { sortedDeviceTypes.indexOf(it.type::class) }
                    .thenBy { it.name }
            )
        }
}