package com.tech.maxclub.bluetoothme.feature.bluetooth.domain.bluetooth.models

sealed class BluetoothState {
    object TurningOff : BluetoothState()
    object Off : BluetoothState()
    object TurningOn : BluetoothState()
    sealed class On(val device: BluetoothDevice?) : BluetoothState() {
        class Connecting(device: BluetoothDevice) : On(device)
        class Connected(device: BluetoothDevice) : On(device)
        class Disconnecting(device: BluetoothDevice) : On(device)
        object Disconnected : On(null)
    }
}