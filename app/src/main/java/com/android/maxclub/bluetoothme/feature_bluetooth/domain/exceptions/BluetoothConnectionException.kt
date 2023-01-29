package com.android.maxclub.bluetoothme.feature_bluetooth.domain.exceptions

import com.android.maxclub.bluetoothme.feature_bluetooth.domain.bluetooth.models.BluetoothDevice

data class BluetoothConnectionException(val bluetoothDevice: BluetoothDevice? = null) : Exception()