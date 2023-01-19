package com.android.maxclub.bluetoothme.domain.bluetooth.model

import android.bluetooth.BluetoothAdapter

sealed class BluetoothState {
    object TurningOff : BluetoothState()
    object TurnOff : BluetoothState()
    object TurningOn : BluetoothState()
    sealed class TurnOn(val device: BluetoothDevice?) : BluetoothState() {
        class Connecting(device: BluetoothDevice) : TurnOn(device)
        class Connected(device: BluetoothDevice) : TurnOn(device)
        class Disconnecting(device: BluetoothDevice) : TurnOn(device)
        object Disconnected : TurnOn(null)
    }
}

fun Int.toBluetoothState(device: BluetoothDevice? = null): BluetoothState =
    when (this) {
        BluetoothAdapter.STATE_ON -> {
            device?.let {
                when (it.state) {
                    is BluetoothDeviceState.Connected -> BluetoothState.TurnOn.Connected(device)
                    is BluetoothDeviceState.Connecting -> BluetoothState.TurnOn.Connecting(device)
                    is BluetoothDeviceState.Disconnecting ->
                        BluetoothState.TurnOn.Disconnecting(device)
                    is BluetoothDeviceState.Disconnected -> BluetoothState.TurnOn.Disconnected
                }
            } ?: BluetoothState.TurnOn.Disconnected
        }
        BluetoothAdapter.STATE_TURNING_OFF -> BluetoothState.TurningOff
        BluetoothAdapter.STATE_TURNING_ON -> BluetoothState.TurningOn
        else -> BluetoothState.TurnOff
    }
