package com.android.maxclub.bluetoothme.presentation.connection

import com.android.maxclub.bluetoothme.domain.bluetooth.model.BluetoothDevice

sealed class ConnectionUiEvent {
    class OnShowMissingPermissionMessage(vararg val permissions: String) : ConnectionUiEvent()
    data class OnShowConnectionErrorMessage(val device: BluetoothDevice) : ConnectionUiEvent()
    object OnShowBluetoothSettings : ConnectionUiEvent()
    data class OnShowDeviceType(val deviceType: String) : ConnectionUiEvent()
    object OnConnected : ConnectionUiEvent()
}
