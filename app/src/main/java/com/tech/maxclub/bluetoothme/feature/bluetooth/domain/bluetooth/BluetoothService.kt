package com.tech.maxclub.bluetoothme.feature.bluetooth.domain.bluetooth

import com.tech.maxclub.bluetoothme.feature.bluetooth.domain.bluetooth.models.BluetoothDevice
import com.tech.maxclub.bluetoothme.feature.bluetooth.domain.messages.Message

interface BluetoothService : BluetoothStateObserver {

    suspend fun connect(device: BluetoothDevice)

    fun disconnect(device: BluetoothDevice? = null)

    fun write(message: Message)
}