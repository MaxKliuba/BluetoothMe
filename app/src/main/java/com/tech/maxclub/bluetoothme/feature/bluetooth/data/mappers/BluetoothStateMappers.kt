package com.tech.maxclub.bluetoothme.feature.bluetooth.data.mappers

import android.bluetooth.BluetoothAdapter
import android.content.Context
import com.tech.maxclub.bluetoothme.R
import com.tech.maxclub.bluetoothme.feature.bluetooth.domain.bluetooth.models.BluetoothDevice
import com.tech.maxclub.bluetoothme.feature.bluetooth.domain.bluetooth.models.BluetoothDeviceState
import com.tech.maxclub.bluetoothme.feature.bluetooth.domain.bluetooth.models.BluetoothState

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
        is BluetoothState.TurningOff -> context.getString(R.string.turning_off_state)
        is BluetoothState.Off -> context.getString(R.string.turned_off_state)
        is BluetoothState.TurningOn -> context.getString(R.string.turning_on_state)
        is BluetoothState.On.Connecting -> context.getString(R.string.connecting_state)
        is BluetoothState.On.Connected -> context.getString(R.string.connected_state)
        is BluetoothState.On.Disconnecting -> context.getString(R.string.disconnecting_state)
        is BluetoothState.On.Disconnected -> context.getString(R.string.disconnected_state)
    }

fun BluetoothState.toFullString(context: Context) =
    when (this) {
        is BluetoothState.TurningOff -> context.getString(R.string.turning_off_message)
        is BluetoothState.Off -> context.getString(R.string.turned_off_message)
        is BluetoothState.TurningOn -> context.getString(R.string.turning_on_message)
        is BluetoothState.On.Connecting -> context.getString(
            R.string.connecting_message, device?.name, device?.address
        )

        is BluetoothState.On.Connected -> context.getString(
            R.string.connected_message, device?.name, device?.address
        )

        is BluetoothState.On.Disconnecting -> context.getString(
            R.string.disconnecting_message, device?.name, device?.address
        )

        is BluetoothState.On.Disconnected -> context.getString(R.string.disconnected_message)
    }