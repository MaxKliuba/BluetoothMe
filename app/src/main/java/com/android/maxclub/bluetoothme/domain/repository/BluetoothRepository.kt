package com.android.maxclub.bluetoothme.domain.repository

import com.android.maxclub.bluetoothme.domain.model.BluetoothState
import com.android.maxclub.bluetoothme.domain.model.BondedDevice
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface BluetoothRepository {
    fun getState(): StateFlow<BluetoothState>

    fun getBondedDevices(): Flow<List<BondedDevice>>

    fun connect(device: BondedDevice)

    fun disconnect(device: BondedDevice)
}