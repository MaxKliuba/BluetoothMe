package com.android.maxclub.bluetoothme.domain.bluetooth

import com.android.maxclub.bluetoothme.domain.bluetooth.model.BluetoothDevice
import com.android.maxclub.bluetoothme.domain.messages.Message

interface BluetoothService : BluetoothStateObserver {

    suspend fun connect(device: BluetoothDevice)

    fun disconnect(device: BluetoothDevice? = null)

    fun write(message: Message)
}