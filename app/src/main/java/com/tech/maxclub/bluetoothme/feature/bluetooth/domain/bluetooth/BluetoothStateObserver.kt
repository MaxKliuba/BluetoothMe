package com.tech.maxclub.bluetoothme.feature.bluetooth.domain.bluetooth

import com.tech.maxclub.bluetoothme.feature.bluetooth.domain.bluetooth.models.BluetoothState
import kotlinx.coroutines.flow.Flow

interface BluetoothStateObserver {

    val initialState: BluetoothState

    fun getState(): Flow<BluetoothState>
}