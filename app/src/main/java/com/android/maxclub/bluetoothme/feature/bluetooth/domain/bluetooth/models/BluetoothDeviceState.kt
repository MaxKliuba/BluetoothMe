package com.android.maxclub.bluetoothme.feature.bluetooth.domain.bluetooth.models

sealed class BluetoothDeviceState {
    object Connecting : BluetoothDeviceState()
    object Connected : BluetoothDeviceState()
    object Disconnecting : BluetoothDeviceState()
    object Disconnected : BluetoothDeviceState()
}