package com.android.maxclub.bluetoothme.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import com.android.maxclub.bluetoothme.domain.exceptions.MissingBluetoothPermissionException

@Throws(MissingBluetoothPermissionException::class)
inline fun <T> withCheckSelfBluetoothPermission(context: Context, block: () -> T): T =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
        ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.BLUETOOTH_CONNECT
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        throw MissingBluetoothPermissionException(Manifest.permission.BLUETOOTH_CONNECT)
    } else {
        block()
    }
