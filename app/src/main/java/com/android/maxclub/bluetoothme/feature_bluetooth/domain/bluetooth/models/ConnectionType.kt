package com.android.maxclub.bluetoothme.feature_bluetooth.domain.bluetooth.models

sealed class ConnectionType {
    object Classic : ConnectionType()
    object Ble : ConnectionType()
}