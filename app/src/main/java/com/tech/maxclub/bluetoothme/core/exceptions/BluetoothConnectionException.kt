package com.tech.maxclub.bluetoothme.core.exceptions

import com.tech.maxclub.bluetoothme.feature.bluetooth.domain.bluetooth.models.BluetoothDevice

data class BluetoothConnectionException(val bluetoothDevice: BluetoothDevice? = null) : Exception()