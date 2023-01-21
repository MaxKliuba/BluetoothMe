package com.android.maxclub.bluetoothme.presentation.connection

import com.android.maxclub.bluetoothme.domain.bluetooth.model.BluetoothDevice

sealed class ConnectionEvent {
    object OnStartScan : ConnectionEvent()
    object OnStopScan : ConnectionEvent()
    object OnClickBluetoothSettings : ConnectionEvent()
    data class OnClickDeviceIcon(val deviceType: String) : ConnectionEvent()
    data class OnUpdateBluetoothDevice(val device: BluetoothDevice) : ConnectionEvent()
    data class OnConnect(val device: BluetoothDevice) : ConnectionEvent()
    data class OnDisconnect(val device: BluetoothDevice) : ConnectionEvent()
}
