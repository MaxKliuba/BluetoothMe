package com.android.maxclub.bluetoothme.util

import android.content.Context
import com.android.maxclub.bluetoothme.R
import com.android.maxclub.bluetoothme.domain.bluetooth.model.BluetoothState

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