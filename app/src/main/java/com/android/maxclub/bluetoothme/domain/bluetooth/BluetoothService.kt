package com.android.maxclub.bluetoothme.domain.bluetooth

import com.android.maxclub.bluetoothme.domain.bluetooth.model.BluetoothDevice
import com.android.maxclub.bluetoothme.domain.messages.Message
import kotlinx.coroutines.flow.Flow
import android.bluetooth.BluetoothDevice as Device

interface BluetoothService : BluetoothStateObserver {

    fun getDevices(): Flow<List<Device>>

    suspend fun connect(device: BluetoothDevice)

    fun disconnect(device: BluetoothDevice? = null)

    fun write(message: Message)
}