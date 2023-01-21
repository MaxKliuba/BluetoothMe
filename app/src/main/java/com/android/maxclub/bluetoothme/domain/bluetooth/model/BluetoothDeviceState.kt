package com.android.maxclub.bluetoothme.domain.bluetooth.model

import android.bluetooth.BluetoothProfile
import androidx.compose.runtime.Immutable
import android.bluetooth.BluetoothDevice as Device

sealed class BluetoothDeviceState {
    object Connecting : BluetoothDeviceState()
    object Connected : BluetoothDeviceState()
    object Disconnecting : BluetoothDeviceState()
    object Disconnected : BluetoothDeviceState()
}

fun BluetoothState.toBluetoothDeviceState(device: Device) =
    if (this is BluetoothState.TurnOn && this.device?.address == device.address) {
        when (this) {
            is BluetoothState.TurnOn.Connecting -> BluetoothDeviceState.Connecting
            is BluetoothState.TurnOn.Connected -> BluetoothDeviceState.Connected
            is BluetoothState.TurnOn.Disconnecting -> BluetoothDeviceState.Disconnecting
            is BluetoothState.TurnOn.Disconnected -> BluetoothDeviceState.Disconnected
        }
    } else {
        BluetoothDeviceState.Disconnected
    }

fun String?.toBluetoothDeviceState() =
    when (this) {
        Device.ACTION_ACL_CONNECTED -> BluetoothDeviceState.Connected
        Device.ACTION_ACL_DISCONNECT_REQUESTED -> BluetoothDeviceState.Disconnecting
        else -> BluetoothDeviceState.Disconnected
    }

fun Int?.toBluetoothDeviceState() =
    when (this) {
        BluetoothProfile.STATE_CONNECTING -> BluetoothDeviceState.Connecting
        BluetoothProfile.STATE_CONNECTED -> BluetoothDeviceState.Connected
        BluetoothProfile.STATE_DISCONNECTING -> BluetoothDeviceState.Disconnecting
        else -> BluetoothDeviceState.Disconnected
    }