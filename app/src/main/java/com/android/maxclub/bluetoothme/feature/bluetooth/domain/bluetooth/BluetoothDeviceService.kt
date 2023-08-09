package com.android.maxclub.bluetoothme.feature.bluetooth.domain.bluetooth

import android.bluetooth.BluetoothDevice
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface BluetoothDeviceService {

    fun getBondedDevices(): Flow<List<BluetoothDevice>>

    fun getScannedDevices(): Flow<List<BluetoothDevice>>

    fun getScanState(): StateFlow<Boolean>

    suspend fun startScan(duration: Long = 10)

    fun stopScan()
}