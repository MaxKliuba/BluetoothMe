package com.android.maxclub.bluetoothme.feature.bluetooth.presentation.connection

import com.android.maxclub.bluetoothme.feature.bluetooth.domain.bluetooth.models.BluetoothDevice
import com.android.maxclub.bluetoothme.feature.bluetooth.domain.bluetooth.models.BluetoothState

data class ConnectionUiState(
    val bluetoothState: BluetoothState,
    val devices: List<BluetoothDevice>,
    val isScanning: Boolean,
) {
    val isLoading: Boolean
        get() = isScanning ||
                bluetoothState is BluetoothState.TurningOn ||
                bluetoothState is BluetoothState.TurningOff ||
                bluetoothState is BluetoothState.On.Connecting ||
                bluetoothState is BluetoothState.On.Disconnecting
}