package com.android.maxclub.bluetoothme.presentation.main

import com.android.maxclub.bluetoothme.domain.bluetooth.model.BluetoothDevice
import com.android.maxclub.bluetoothme.domain.bluetooth.model.BluetoothState

data class MainUiState(
    val bluetoothState: BluetoothState,
    val favoriteBluetoothDevice: BluetoothDevice?,
    val controllersCount: Int,
    val inputMessagesCount: Int,
    val outputMessagesCount: Int,
) {
    val messagesCount: Int
        get() = inputMessagesCount + outputMessagesCount
}