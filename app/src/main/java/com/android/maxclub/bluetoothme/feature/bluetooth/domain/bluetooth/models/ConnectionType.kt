package com.android.maxclub.bluetoothme.feature.bluetooth.domain.bluetooth.models

sealed class ConnectionType {
    object Classic : ConnectionType()
    data class Ble(
        val profile: BluetoothLeProfile = BluetoothLeProfile.Default
    ) : ConnectionType()
}