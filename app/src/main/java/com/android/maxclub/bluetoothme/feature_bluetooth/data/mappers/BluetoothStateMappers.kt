package com.android.maxclub.bluetoothme.feature_bluetooth.data.mappers

import android.bluetooth.BluetoothAdapter
import android.content.Context
import com.android.maxclub.bluetoothme.R
import com.android.maxclub.bluetoothme.feature_bluetooth.domain.bluetooth.models.BluetoothDevice
import com.android.maxclub.bluetoothme.feature_bluetooth.domain.bluetooth.models.BluetoothDeviceState
import com.android.maxclub.bluetoothme.feature_bluetooth.domain.bluetooth.models.BluetoothState

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

fun BluetoothState.toString(context: Context) =
    when (this) {
        is BluetoothState.TurningOff -> context.getString(R.string.state_turning_off)
        is BluetoothState.Off -> context.getString(R.string.state_off)
        is BluetoothState.TurningOn -> context.getString(R.string.state_turning_on)
        is BluetoothState.On.Connecting -> context.getString(R.string.state_connecting)
        is BluetoothState.On.Connected -> context.getString(R.string.state_connected)
        is BluetoothState.On.Disconnecting -> context.getString(R.string.state_disconnecting)
        is BluetoothState.On.Disconnected -> context.getString(R.string.state_disconnected)
    }