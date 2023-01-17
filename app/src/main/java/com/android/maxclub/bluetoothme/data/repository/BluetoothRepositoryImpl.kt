package com.android.maxclub.bluetoothme.data.repository

import android.content.Context
import com.android.maxclub.bluetoothme.data.bluetooth.BluetoothService
import com.android.maxclub.bluetoothme.domain.exception.MissingBluetoothPermissionException
import com.android.maxclub.bluetoothme.domain.model.BluetoothState
import com.android.maxclub.bluetoothme.domain.model.BondedDevice
import com.android.maxclub.bluetoothme.domain.model.BondedDeviceState
import com.android.maxclub.bluetoothme.domain.model.toBondedDevice
import com.android.maxclub.bluetoothme.domain.repository.BluetoothRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BluetoothRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val service: BluetoothService
) : BluetoothRepository {
    private var bondedDevices: List<BondedDevice> = emptyList()

    override fun getState(): StateFlow<BluetoothState> = service.getState()

    @Throws(MissingBluetoothPermissionException::class)
    override fun getBondedDevices(): Flow<List<BondedDevice>> =
        service.getBondedDevices().combine(getState()) { devices, state ->
//            return@combine listOf(
//                BondedDevice("0", "name0", DeviceType.Unknown(ConnectionType.CLASSIC), BondedDeviceState.Disconnected),
//                BondedDevice("1", "name1", DeviceType.Dual(ConnectionType.CLASSIC), BondedDeviceState.Disconnecting),
//                BondedDevice("2", "name2", DeviceType.Unknown(ConnectionType.CLASSIC), BondedDeviceState.Disconnected),
//                BondedDevice("3", "name3", DeviceType.Classic, BondedDeviceState.Disconnected),
//                BondedDevice("4", "name4", DeviceType.Dual(ConnectionType.BLE), BondedDeviceState.Connecting),
//                BondedDevice("5", "name5", DeviceType.Unknown(ConnectionType.BLE), BondedDeviceState.Connected),
//                BondedDevice("6", "name6", DeviceType.BLE, BondedDeviceState.Disconnected),
//                BondedDevice("7", "name7", DeviceType.Classic, BondedDeviceState.Disconnected),
//            )

            when (state) {
                is BluetoothState.TurnOn -> {
                    devices.map { bluetoothDevice ->
                        val deviceState = when (state) {
                            is BluetoothState.TurnOn.Disconnected -> {
                                BondedDeviceState.Disconnected
                            }
                            is BluetoothState.TurnOn.Disconnecting -> {
                                if (state.device?.address == bluetoothDevice.address) {
                                    BondedDeviceState.Disconnecting
                                } else {
                                    BondedDeviceState.Disconnected
                                }
                            }
                            is BluetoothState.TurnOn.Connecting -> {
                                if (state.device?.address == bluetoothDevice.address) {
                                    BondedDeviceState.Connecting
                                } else {
                                    BondedDeviceState.Disconnected
                                }
                            }
                            is BluetoothState.TurnOn.Connected -> {
                                if (state.device?.address == bluetoothDevice.address) {
                                    BondedDeviceState.Connected
                                } else {
                                    BondedDeviceState.Disconnected
                                }
                            }
                        }

                        bondedDevices.find { bondedDevice ->
                            bondedDevice.address == bluetoothDevice.address
                        }?.copy(state = deviceState) ?: bluetoothDevice.toBondedDevice(
                            context = context,
                            state = deviceState,
                        )
                    }
                }
                else -> emptyList()
            }
        }.onEach { bondedDevices = it }

    override suspend fun connect(device: BondedDevice) = service.connect(device)

    override fun disconnect(device: BondedDevice) = service.disconnect(device)
}