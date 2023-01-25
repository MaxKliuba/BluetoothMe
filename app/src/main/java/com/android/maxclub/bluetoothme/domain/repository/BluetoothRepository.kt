package com.android.maxclub.bluetoothme.domain.repository

import com.android.maxclub.bluetoothme.domain.bluetooth.model.BluetoothDevice
import com.android.maxclub.bluetoothme.domain.bluetooth.model.BluetoothState
import com.android.maxclub.bluetoothme.domain.messages.Message
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface BluetoothRepository {

    fun getState(): StateFlow<BluetoothState>

    fun getBluetoothDevices(): Flow<List<BluetoothDevice>>

    fun updateBluetoothDevice(device: BluetoothDevice)

    fun getFavoriteBluetoothDevice(): StateFlow<BluetoothDevice?>

    fun getScanState(): StateFlow<Boolean>

    suspend fun startScan(duration: Long)

    fun stopScan()

    fun enableAdapter()

    suspend fun connect(device: BluetoothDevice)

    fun disconnect(device: BluetoothDevice? = null)

    fun writeMessage(message: Message)

    fun getMessages(): Flow<List<Message>>
}