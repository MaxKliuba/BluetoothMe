package com.tech.maxclub.bluetoothme.feature.bluetooth.domain.bluetooth.models

data class BluetoothDevice(
    val address: String,
    val name: String,
    val isBonded: Boolean,
    val type: DeviceType,
    val state: BluetoothDeviceState,
    val isFavorite: Boolean = false,
)