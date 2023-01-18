package com.android.maxclub.bluetoothme.domain.bluetooth.model

sealed class BluetoothDeviceState {
    object Connecting : BluetoothDeviceState()
    object Connected : BluetoothDeviceState()
    object Disconnecting : BluetoothDeviceState()
    object Disconnected : BluetoothDeviceState()
}