package com.tech.maxclub.bluetoothme.feature.bluetooth.domain.usecases.bluetooth

import com.tech.maxclub.bluetoothme.feature.bluetooth.domain.bluetooth.models.BluetoothDevice
import com.tech.maxclub.bluetoothme.feature.bluetooth.domain.bluetooth.models.BluetoothDeviceState
import com.tech.maxclub.bluetoothme.feature.bluetooth.domain.bluetooth.models.DeviceType
import com.tech.maxclub.bluetoothme.feature.bluetooth.domain.repositories.BluetoothRepository
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

    operator fun invoke(): Flow<List<BluetoothDevice>> =
        repository.getBluetoothDevices()
        .map { bluetoothDevices ->
            bluetoothDevices.sortedWith(
                compareBy<BluetoothDevice> { it.state !is BluetoothDeviceState.Connected }
                    .thenBy { sortedDeviceTypes.indexOf(it.type::class) }
                    .thenBy { it.name }
            )
        }
//        flowOf(
//            (0..100).map {
//                BluetoothDevice(
//                    address = "Address $it",
//                    name = "Name $it",
//                    isBonded = true,
//                    type = listOf(
//                        DeviceType.Classic(ConnectionType.Classic),
//                        DeviceType.Ble(ConnectionType.Ble()),
//                        DeviceType.Dual(),
//                        DeviceType.Unknown(),
//                    ).random(),
//                    state = BluetoothDeviceState.Disconnected,
//                    isFavorite = false,
//                )
//            }
//        )
//            .flowOn(Dispatchers.IO)
}