package com.tech.maxclub.bluetoothme.feature.bluetooth.presentation.connection

import com.tech.maxclub.bluetoothme.feature.bluetooth.domain.bluetooth.models.BluetoothDevice
import com.tech.maxclub.bluetoothme.feature.bluetooth.domain.bluetooth.models.BluetoothState
import com.tech.maxclub.bluetoothme.feature.bluetooth.presentation.connection.util.BleProfileDialogData

data class ConnectionUiState(
    val bluetoothState: BluetoothState,
    val devices: List<BluetoothDevice>,
    val isScanning: Boolean,
    val bleProfileDialogData: BleProfileDialogData?,
) {
    val isLoading: Boolean
        get() = isScanning ||
                bluetoothState is BluetoothState.TurningOn ||
                bluetoothState is BluetoothState.TurningOff ||
                bluetoothState is BluetoothState.On.Connecting ||
                bluetoothState is BluetoothState.On.Disconnecting
}