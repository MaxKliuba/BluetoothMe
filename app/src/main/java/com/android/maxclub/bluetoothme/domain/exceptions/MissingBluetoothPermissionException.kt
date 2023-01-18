package com.android.maxclub.bluetoothme.domain.exceptions

data class MissingBluetoothPermissionException(val permission: String) :
    Exception("Bluetooth permission is missing: $permission")