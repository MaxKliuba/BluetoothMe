package com.android.maxclub.bluetoothme.feature.bluetooth.presentation.main

import com.android.maxclub.bluetoothme.feature.bluetooth.domain.bluetooth.models.BluetoothDevice
import com.android.maxclub.bluetoothme.feature.bluetooth.domain.bluetooth.models.BluetoothState

data class MainUiState(
    val bluetoothState: BluetoothState,
    val favoriteBluetoothDevice: BluetoothDevice?,
    val controllersCount: Int,
    val inputMessagesCount: Int,
    val outputMessagesCount: Int,
    val selectedNavDrawerItem: String,
    val isBluetoothPermissionRationaleDialogOpen: Boolean,
) {
    val messagesCount: Int
        get() = inputMessagesCount + outputMessagesCount
}