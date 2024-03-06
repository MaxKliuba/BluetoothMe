package com.tech.maxclub.bluetoothme.feature.bluetooth.domain.bluetooth.models

sealed class BluetoothDeviceState {
    data object Connecting : BluetoothDeviceState()
    data object Connected : BluetoothDeviceState()
    data object Disconnecting : BluetoothDeviceState()
    data object Disconnected : BluetoothDeviceState()
}