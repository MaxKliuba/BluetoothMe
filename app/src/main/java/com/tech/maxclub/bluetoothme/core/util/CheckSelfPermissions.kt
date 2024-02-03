package com.tech.maxclub.bluetoothme.core.util

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.provider.Settings
import androidx.core.app.ActivityCompat
import com.tech.maxclub.bluetoothme.core.exceptions.EnableLocationException
import com.tech.maxclub.bluetoothme.core.exceptions.MissingBluetoothPermissionException
import com.tech.maxclub.bluetoothme.core.exceptions.MissingLocationPermissionException

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

@Throws(MissingBluetoothPermissionException::class, MissingLocationPermissionException::class)
inline fun <T> withCheckSelfBluetoothScanPermission(context: Context, block: () -> T): T {
    val requireLocationPermissions = Build.VERSION.SDK_INT < Build.VERSION_CODES.S

    val requiredPermissions = if (requireLocationPermissions) {
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
        throw if (requireLocationPermissions) {
            MissingLocationPermissionException(*missingPermissions.toTypedArray())
        } else {
            MissingBluetoothPermissionException(*missingPermissions.toTypedArray())
        }
    }

    return block()
}

@Throws(EnableLocationException::class)
inline fun <T> withCheckLocationAccess(context: Context, block: () -> T): T {
    val requireLocationAccess = Build.VERSION.SDK_INT < Build.VERSION_CODES.S

    if (requireLocationAccess) {
        val isLocationEnabled =
            (context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager)
                ?.let { locationManager ->
                    locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                            locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
                } ?: false

        if (!isLocationEnabled) {
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)

            throw EnableLocationException(intent)
        }
    }

    return block()
}