package com.android.maxclub.bluetoothme.domain.bluetooth.model

import android.bluetooth.BluetoothProfile
import android.bluetooth.BluetoothDevice as Device

sealed class BluetoothDeviceState {
    object Connecting : BluetoothDeviceState()
    object Connected : BluetoothDeviceState()
    object Disconnecting : BluetoothDeviceState()
    object Disconnected : BluetoothDeviceState()
}

fun BluetoothState.toBluetoothDeviceState(device: Device) =
    if (this is BluetoothState.On && this.device?.address == device.address) {
        when (this) {
            is BluetoothState.On.Connecting -> BluetoothDeviceState.Connecting
            is BluetoothState.On.Connected -> BluetoothDeviceState.Connected
            is BluetoothState.On.Disconnecting -> BluetoothDeviceState.Disconnecting
            is BluetoothState.On.Disconnected -> BluetoothDeviceState.Disconnected
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