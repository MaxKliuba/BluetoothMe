package com.tech.maxclub.bluetoothme.feature.bluetooth.domain.bluetooth.models

sealed class BluetoothState {
    data object TurningOff : BluetoothState()
    data object Off : BluetoothState()
    data object TurningOn : BluetoothState()
    sealed class On(val device: BluetoothDevice?) : BluetoothState() {
        class Connecting(device: BluetoothDevice) : On(device)
        class Connected(device: BluetoothDevice) : On(device)
        class Disconnecting(device: BluetoothDevice) : On(device)
        data object Disconnected : On(null)
    }
}