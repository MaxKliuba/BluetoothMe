package com.android.maxclub.bluetoothme.domain.exceptions

class MissingBluetoothPermissionException(vararg permissions: String) :
    Exception("Bluetooth permission is missing: ${permissions.joinToString()}")