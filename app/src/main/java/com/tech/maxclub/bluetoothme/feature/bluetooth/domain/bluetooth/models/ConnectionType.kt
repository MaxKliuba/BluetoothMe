package com.tech.maxclub.bluetoothme.feature.bluetooth.domain.bluetooth.models

sealed class ConnectionType {
    data object Classic : ConnectionType()
    data class Ble(
        val profile: BluetoothLeProfile = BluetoothLeProfile.Default
    ) : ConnectionType()
}