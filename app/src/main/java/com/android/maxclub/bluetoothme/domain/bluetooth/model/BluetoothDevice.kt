package com.android.maxclub.bluetoothme.domain.bluetooth.model

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.runtime.Immutable
import com.android.maxclub.bluetoothme.util.withCheckSelfBluetoothPermission
import android.bluetooth.BluetoothDevice as Device

data class BluetoothDevice(
    val address: String,
    val name: String,
    val isBonded: Boolean,
    val type: DeviceType,
    val state: BluetoothDeviceState,
    val isFavorite: Boolean = false,
)

@SuppressLint("MissingPermission")
fun Device.toBluetoothDevice(
    context: Context,
    state: BluetoothDeviceState,
    connectionType: ConnectionType = ConnectionType.Classic,
): BluetoothDevice =
    withCheckSelfBluetoothPermission(context) {
        BluetoothDevice(
            address = address,
            name = name ?: "Unknown",
            isBonded = bondState == Device.BOND_BONDED,
            type = when (type) {
                Device.DEVICE_TYPE_CLASSIC -> DeviceType.Classic
                Device.DEVICE_TYPE_LE -> DeviceType.Ble
                Device.DEVICE_TYPE_DUAL -> DeviceType.Dual(connectionType)
                else -> DeviceType.Unknown(connectionType)
            },
            state = state
        )
    }