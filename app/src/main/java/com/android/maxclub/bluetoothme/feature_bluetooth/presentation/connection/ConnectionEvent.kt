package com.android.maxclub.bluetoothme.feature_bluetooth.presentation.connection

import com.android.maxclub.bluetoothme.feature_bluetooth.domain.bluetooth.models.BluetoothDevice

sealed class ConnectionEvent {
    object OnEnableAdapter : ConnectionEvent()
    object OnStartScan : ConnectionEvent()
    object OnStopScan : ConnectionEvent()
    object OnOpenBluetoothSettings : ConnectionEvent()
    data class OnClickDeviceIcon(val deviceType: String) : ConnectionEvent()
    data class OnUpdateBluetoothDevice(val device: BluetoothDevice) : ConnectionEvent()
    data class OnConnect(val device: BluetoothDevice) : ConnectionEvent()
    data class OnDisconnect(val device: BluetoothDevice?) : ConnectionEvent()
}
