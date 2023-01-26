package com.android.maxclub.bluetoothme.feature_bluetooth.presentation.connection

import com.android.maxclub.bluetoothme.feature_bluetooth.domain.bluetooth.models.BluetoothDevice

sealed class ConnectionUiEvent {
    class OnShowMissingPermissionMessage(vararg val permissions: String) : ConnectionUiEvent()
    data class OnShowConnectionErrorMessage(val device: BluetoothDevice) : ConnectionUiEvent()
    object OnOpenBluetoothSettings : ConnectionUiEvent()
    data class OnShowDeviceType(val deviceType: String) : ConnectionUiEvent()
    data class OnConnected(val device: BluetoothDevice) : ConnectionUiEvent()
}