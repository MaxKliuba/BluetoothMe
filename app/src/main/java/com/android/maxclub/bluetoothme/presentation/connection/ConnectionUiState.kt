package com.android.maxclub.bluetoothme.presentation.connection

import com.android.maxclub.bluetoothme.domain.bluetooth.model.BluetoothDevice
import com.android.maxclub.bluetoothme.domain.bluetooth.model.BluetoothState

data class ConnectionUiState(
    val bluetoothState: BluetoothState,
    val devices: List<BluetoothDevice>,
    val isScanning: Boolean,
) {
    val isLoading: Boolean
        get() = isScanning ||
                bluetoothState is BluetoothState.TurningOn ||
                bluetoothState is BluetoothState.TurningOff ||
                bluetoothState is BluetoothState.TurnOn.Connecting ||
                bluetoothState is BluetoothState.TurnOn.Disconnecting
}
