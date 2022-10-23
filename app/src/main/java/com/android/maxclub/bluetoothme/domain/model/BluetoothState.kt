package com.android.maxclub.bluetoothme.domain.model

import android.bluetooth.BluetoothAdapter

sealed class BluetoothState {
    object TurnOff : BluetoothState()
    object TurningOff : BluetoothState()
    object TurningOn : BluetoothState()
    sealed class TurnOn(val device: BondedDevice?) : BluetoothState() {
        object Disconnected : TurnOn(null)
        class Disconnecting(device: BondedDevice) : TurnOn(device)
        class Connecting(device: BondedDevice) : TurnOn(device)
        class Connected(device: BondedDevice) : TurnOn(device)
    }
}

fun Int.toBluetoothState(device: BondedDevice? = null): BluetoothState =
    when (this) {
        BluetoothAdapter.STATE_ON -> {
            device?.let {
                when (it.state) {
                    is BondedDeviceState.Connected -> BluetoothState.TurnOn.Connected(device)
                    is BondedDeviceState.Connecting -> BluetoothState.TurnOn.Connecting(device)
                    is BondedDeviceState.Disconnecting -> BluetoothState.TurnOn.Disconnecting(device)
                    is BondedDeviceState.Disconnected -> BluetoothState.TurnOn.Disconnected
                }
            } ?: BluetoothState.TurnOn.Disconnected
        }
        BluetoothAdapter.STATE_TURNING_OFF -> BluetoothState.TurningOff
        BluetoothAdapter.STATE_TURNING_ON -> BluetoothState.TurningOn
        else -> BluetoothState.TurnOff
    }
