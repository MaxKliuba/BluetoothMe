package com.android.maxclub.bluetoothme.domain.bluetooth.model

data class DiscoveredDevice(
    val address: String,
    val name: String,
    val type: DeviceType,
    val state: BluetoothDeviceState,
)