package com.android.maxclub.bluetoothme.core.exceptions

class MissingBluetoothPermissionException(vararg val permissions: String) :
    Exception("Bluetooth permission is missing: ${permissions.joinToString()}")