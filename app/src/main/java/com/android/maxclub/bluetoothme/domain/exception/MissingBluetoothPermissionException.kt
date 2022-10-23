package com.android.maxclub.bluetoothme.domain.exception

class MissingBluetoothPermissionException(permission: String) :
    Exception("Bluetooth permission is missing: $permission")