package com.android.maxclub.bluetoothme.domain.exceptions

import com.android.maxclub.bluetoothme.domain.bluetooth.model.BluetoothDevice

data class BluetoothConnectionException(val bluetoothDevice: BluetoothDevice) : Exception()