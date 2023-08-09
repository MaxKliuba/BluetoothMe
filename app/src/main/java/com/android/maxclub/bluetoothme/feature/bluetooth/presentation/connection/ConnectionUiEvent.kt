package com.android.maxclub.bluetoothme.feature.bluetooth.presentation.connection

import com.android.maxclub.bluetoothme.feature.bluetooth.domain.bluetooth.models.BluetoothDevice

sealed class ConnectionUiEvent {
    object OnStartScan : ConnectionUiEvent()
    object OnStopScan : ConnectionUiEvent()
    object OnOpenBluetoothSettings : ConnectionUiEvent()
    data class OnClickDeviceIcon(val deviceType: String) : ConnectionUiEvent()
    data class OnUpdateBluetoothDevice(val device: BluetoothDevice) : ConnectionUiEvent()
}