package com.android.maxclub.bluetoothme.domain.model

import android.Manifest
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import com.android.maxclub.bluetoothme.domain.exception.MissingBluetoothPermissionException

data class BondedDevice(
    val address: String,
    val name: String,
    val type: DeviceType,
    val state: BondedDeviceState,
)

@Throws(MissingBluetoothPermissionException::class)
fun BluetoothDevice.toBondedDevice(context: Context, state: BondedDeviceState): BondedDevice =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
        ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.BLUETOOTH_CONNECT
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        throw MissingBluetoothPermissionException(Manifest.permission.BLUETOOTH_CONNECT)
    } else {
        BondedDevice(
            address = address,
            name = name,
            type = when (type) {
                BluetoothDevice.DEVICE_TYPE_CLASSIC -> DeviceType.Classic
                BluetoothDevice.DEVICE_TYPE_LE -> DeviceType.BLE
                BluetoothDevice.DEVICE_TYPE_DUAL -> DeviceType.Dual(ConnectionType.CLASSIC)
                else -> DeviceType.Unknown(ConnectionType.CLASSIC)
            },
            state = state
        )
    }