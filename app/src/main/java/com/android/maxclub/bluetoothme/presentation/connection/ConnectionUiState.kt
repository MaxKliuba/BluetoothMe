package com.android.maxclub.bluetoothme.presentation.connection

import com.android.maxclub.bluetoothme.domain.model.BluetoothState
import com.android.maxclub.bluetoothme.domain.model.BondedDevice

data class ConnectionUiState(
    val state: BluetoothState,
    val devices: List<BondedDevice>,
)
