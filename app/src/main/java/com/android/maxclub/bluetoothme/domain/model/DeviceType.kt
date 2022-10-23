package com.android.maxclub.bluetoothme.domain.model

sealed class DeviceType(val connectionType: ConnectionType) {
    object Classic : DeviceType(ConnectionType.CLASSIC)
    object BLE : DeviceType(ConnectionType.BLE)
    class Dual(connectionType: ConnectionType) : DeviceType(connectionType)
    class Unknown(connectionType: ConnectionType) : DeviceType(connectionType)
}