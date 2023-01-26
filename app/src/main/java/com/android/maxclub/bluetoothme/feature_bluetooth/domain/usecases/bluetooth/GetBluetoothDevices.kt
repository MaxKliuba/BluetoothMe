package com.android.maxclub.bluetoothme.feature_bluetooth.domain.usecases.bluetooth

import com.android.maxclub.bluetoothme.feature_bluetooth.domain.bluetooth.models.BluetoothDevice
import com.android.maxclub.bluetoothme.feature_bluetooth.domain.bluetooth.models.BluetoothDeviceState
import com.android.maxclub.bluetoothme.feature_bluetooth.domain.bluetooth.models.DeviceType
import com.android.maxclub.bluetoothme.feature_bluetooth.domain.repositories.BluetoothRepository
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