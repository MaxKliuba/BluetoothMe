package com.android.maxclub.bluetoothme.feature_bluetooth.domain.exceptions

class MissingBluetoothPermissionException(vararg val permissions: String) :
    Exception("Bluetooth permission is missing: ${permissions.joinToString()}")