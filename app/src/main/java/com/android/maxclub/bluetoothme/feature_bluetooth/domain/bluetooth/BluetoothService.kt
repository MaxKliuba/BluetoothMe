package com.android.maxclub.bluetoothme.feature_bluetooth.domain.bluetooth

import com.android.maxclub.bluetoothme.feature_bluetooth.domain.bluetooth.models.BluetoothDevice
import com.android.maxclub.bluetoothme.feature_bluetooth.domain.messages.Message

interface BluetoothService : BluetoothStateObserver {

    suspend fun connect(device: BluetoothDevice)

    fun disconnect(device: BluetoothDevice? = null)

    fun write(message: Message)
}