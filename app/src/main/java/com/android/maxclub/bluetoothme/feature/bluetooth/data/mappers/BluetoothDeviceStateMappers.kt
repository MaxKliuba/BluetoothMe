package com.android.maxclub.bluetoothme.feature.bluetooth.data.mappers

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothProfile
import com.android.maxclub.bluetoothme.feature.bluetooth.domain.bluetooth.models.BluetoothDeviceState
import com.android.maxclub.bluetoothme.feature.bluetooth.domain.bluetooth.models.BluetoothState

fun BluetoothState.toBluetoothDeviceState(device: BluetoothDevice) =
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
        BluetoothDevice.ACTION_ACL_CONNECTED -> BluetoothDeviceState.Connected
        BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED -> BluetoothDeviceState.Disconnecting
        else -> BluetoothDeviceState.Disconnected
    }

fun Int?.toBluetoothDeviceState() =
    when (this) {
        BluetoothProfile.STATE_CONNECTING -> BluetoothDeviceState.Connecting
        BluetoothProfile.STATE_CONNECTED -> BluetoothDeviceState.Connected
        BluetoothProfile.STATE_DISCONNECTING -> BluetoothDeviceState.Disconnecting
        else -> BluetoothDeviceState.Disconnected
    }