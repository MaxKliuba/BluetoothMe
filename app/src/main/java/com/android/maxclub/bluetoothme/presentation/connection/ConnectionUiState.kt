package com.android.maxclub.bluetoothme.presentation.connection

import com.android.maxclub.bluetoothme.domain.bluetooth.model.BluetoothDevice
import com.android.maxclub.bluetoothme.domain.bluetooth.model.BluetoothState

data class ConnectionUiState(
    val state: BluetoothState,
    val devices: List<BluetoothDevice>,
)
