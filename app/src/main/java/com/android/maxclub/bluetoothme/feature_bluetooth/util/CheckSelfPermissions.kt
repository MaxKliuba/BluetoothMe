package com.android.maxclub.bluetoothme.feature_bluetooth.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import com.android.maxclub.bluetoothme.feature_bluetooth.domain.exceptions.MissingBluetoothPermissionException

@Throws(MissingBluetoothPermissionException::class)
inline fun <T> withCheckSelfBluetoothPermission(context: Context, block: () -> T): T {
    val requiredPermissions = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
        emptyList()
    } else {
        listOf(Manifest.permission.BLUETOOTH_CONNECT)
    }

    val missingPermissions = requiredPermissions.filter { permission ->
        ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED
    }

    if (missingPermissions.isNotEmpty()) {
        throw MissingBluetoothPermissionException(*missingPermissions.toTypedArray())
    }

    return block()
}

@Throws(MissingBluetoothPermissionException::class)
inline fun <T> withCheckSelfBluetoothScanPermission(context: Context, block: () -> T): T {
    val requiredPermissions = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
        listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
        )
    } else {
        listOf(
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_SCAN
        )
    }

    val missingPermissions = requiredPermissions.filter { permission ->
        ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED
    }

    if (missingPermissions.isNotEmpty()) {
        throw MissingBluetoothPermissionException(*missingPermissions.toTypedArray())
    }

    return block()
}
