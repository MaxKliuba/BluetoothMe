package com.android.maxclub.bluetoothme.domain.usecase

import com.android.maxclub.bluetoothme.domain.exception.MissingBluetoothPermissionException
import com.android.maxclub.bluetoothme.domain.model.BondedDevice
import com.android.maxclub.bluetoothme.domain.model.BondedDeviceState
import com.android.maxclub.bluetoothme.domain.model.DeviceType
import com.android.maxclub.bluetoothme.domain.repository.BluetoothRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetBondedDevices @Inject constructor(
    private val repository: BluetoothRepository
) {
    private val sortedDeviceTypes = listOf(
        DeviceType.Classic::class,
        DeviceType.BLE::class,
        DeviceType.Dual::class,
        DeviceType.Unknown::class,
    )

    @Throws(MissingBluetoothPermissionException::class)
    operator fun invoke(): Flow<List<BondedDevice>> = repository.getBondedDevices()
        .map { bondedDevices ->
            bondedDevices.sortedWith(
                compareBy<BondedDevice> { it.state !is BondedDeviceState.Connected }
                    .thenBy { sortedDeviceTypes.indexOf(it.type::class) }
            )
        }
}