package com.android.maxclub.bluetoothme.feature.bluetooth.domain.repositories

import com.android.maxclub.bluetoothme.feature.bluetooth.domain.bluetooth.models.BluetoothDevice
import com.android.maxclub.bluetoothme.feature.bluetooth.domain.bluetooth.models.BluetoothState
import com.android.maxclub.bluetoothme.feature.bluetooth.domain.messages.Message
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

    fun getMessages(): Flow<List<Message>>

    fun writeMessage(message: Message)

    fun addMessage(message: Message)

    fun deleteMessages()
}