package com.android.maxclub.bluetoothme.core.exceptions

import com.android.maxclub.bluetoothme.feature.bluetooth.domain.bluetooth.models.BluetoothDevice

data class BluetoothConnectionException(val bluetoothDevice: BluetoothDevice? = null) : Exception()