package com.android.maxclub.bluetoothme.feature_bluetooth.domain.bluetooth.models

import android.bluetooth.BluetoothAdapter

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

fun Int.toBluetoothState(device: BluetoothDevice? = null): BluetoothState =
    when (this) {
        BluetoothAdapter.STATE_ON -> {
            device?.let {
                when (it.state) {
                    is BluetoothDeviceState.Connected -> BluetoothState.On.Connected(device)
                    is BluetoothDeviceState.Connecting -> BluetoothState.On.Connecting(device)
                    is BluetoothDeviceState.Disconnecting -> BluetoothState.On.Disconnecting(device)
                    is BluetoothDeviceState.Disconnected -> BluetoothState.On.Disconnected
                }
            } ?: BluetoothState.On.Disconnected
        }
        BluetoothAdapter.STATE_TURNING_OFF -> BluetoothState.TurningOff
        BluetoothAdapter.STATE_TURNING_ON -> BluetoothState.TurningOn
        else -> BluetoothState.Off
    }
