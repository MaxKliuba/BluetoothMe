package com.android.maxclub.bluetoothme.feature_bluetooth.data.mappers

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.content.Context
import com.android.maxclub.bluetoothme.feature_bluetooth.domain.bluetooth.models.BluetoothDeviceState
import com.android.maxclub.bluetoothme.feature_bluetooth.domain.bluetooth.models.ConnectionType
import com.android.maxclub.bluetoothme.feature_bluetooth.domain.bluetooth.models.DeviceType
import com.android.maxclub.bluetoothme.feature_bluetooth.util.withCheckSelfBluetoothPermission

@SuppressLint("MissingPermission")
fun BluetoothDevice.toBluetoothDevice(
    context: Context,
    state: BluetoothDeviceState,
    connectionType: ConnectionType = ConnectionType.Classic,
): com.android.maxclub.bluetoothme.feature_bluetooth.domain.bluetooth.models.BluetoothDevice =
    withCheckSelfBluetoothPermission(context) {
        com.android.maxclub.bluetoothme.feature_bluetooth.domain.bluetooth.models.BluetoothDevice(
            address = address,
            name = name ?: "Unknown",
            isBonded = bondState == BluetoothDevice.BOND_BONDED,
            type = when (type) {
                BluetoothDevice.DEVICE_TYPE_CLASSIC -> DeviceType.Classic(
                    (connectionType as? ConnectionType.Classic) ?: ConnectionType.Classic
                )
                BluetoothDevice.DEVICE_TYPE_LE -> DeviceType.Ble(
                    (connectionType as? ConnectionType.Ble) ?: ConnectionType.Ble()
                )
                BluetoothDevice.DEVICE_TYPE_DUAL -> DeviceType.Dual(connectionType)
                else -> DeviceType.Unknown(connectionType)
            },
            state = state
        )
    }