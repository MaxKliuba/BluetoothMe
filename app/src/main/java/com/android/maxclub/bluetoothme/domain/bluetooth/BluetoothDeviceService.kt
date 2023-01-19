package com.android.maxclub.bluetoothme.domain.bluetooth

import android.bluetooth.BluetoothDevice
import kotlinx.coroutines.flow.Flow

interface BluetoothDeviceService {

    fun getBondedDevices(): Flow<List<BluetoothDevice>>

    fun getScannedDevices(): Flow<List<BluetoothDevice>>

    suspend fun startScan(duration: Long = 10)

    fun stopScan()
}