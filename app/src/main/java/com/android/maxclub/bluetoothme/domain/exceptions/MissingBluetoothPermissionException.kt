package com.android.maxclub.bluetoothme.domain.exceptions

class MissingBluetoothPermissionException(vararg val permissions: String) :
    Exception("Bluetooth permission is missing: ${permissions.joinToString()}")