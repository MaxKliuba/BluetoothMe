package com.android.maxclub.bluetoothme.feature.bluetooth.data.mappers

import android.annotation.SuppressLint
import com.android.maxclub.bluetoothme.feature.bluetooth.domain.bluetooth.models.BluetoothDevice
import com.android.maxclub.bluetoothme.feature.bluetooth.domain.bluetooth.models.BluetoothDeviceState
import com.android.maxclub.bluetoothme.feature.bluetooth.domain.bluetooth.models.ConnectionType
import com.android.maxclub.bluetoothme.feature.bluetooth.domain.bluetooth.models.DeviceType
import android.bluetooth.BluetoothDevice as AndroidBluetoothDevice

@SuppressLint("MissingPermission")
fun AndroidBluetoothDevice.toBluetoothDevice(
    state: BluetoothDeviceState,
    connectionType: ConnectionType = ConnectionType.Classic,
): BluetoothDevice =
    BluetoothDevice(
        address = address,
        name = name ?: "Unknown",
        isBonded = bondState == AndroidBluetoothDevice.BOND_BONDED,
        type = when (type) {
            AndroidBluetoothDevice.DEVICE_TYPE_CLASSIC -> DeviceType.Classic(
                (connectionType as? ConnectionType.Classic) ?: ConnectionType.Classic
            )

            AndroidBluetoothDevice.DEVICE_TYPE_LE -> DeviceType.Ble(
                (connectionType as? ConnectionType.Ble) ?: ConnectionType.Ble()
            )

            AndroidBluetoothDevice.DEVICE_TYPE_DUAL -> DeviceType.Dual(connectionType)
            else -> DeviceType.Unknown(connectionType)
        },
        state = state
    )