package com.android.maxclub.bluetoothme.feature_bluetooth.presentation.main

import com.android.maxclub.bluetoothme.feature_bluetooth.domain.bluetooth.models.BluetoothDevice
import com.android.maxclub.bluetoothme.feature_bluetooth.domain.bluetooth.models.BluetoothState

data class MainUiState(
    val selectedNavDrawerItem: String,
    val bluetoothState: BluetoothState,
    val favoriteBluetoothDevice: BluetoothDevice?,
    val controllersCount: Int,
    val inputMessagesCount: Int,
    val outputMessagesCount: Int,
) {
    val messagesCount: Int
        get() = inputMessagesCount + outputMessagesCount
}