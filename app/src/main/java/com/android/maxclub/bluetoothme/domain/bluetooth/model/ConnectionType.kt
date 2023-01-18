package com.android.maxclub.bluetoothme.domain.bluetooth.model

sealed class ConnectionType {
    object Classic : ConnectionType()
    object Ble : ConnectionType()
}