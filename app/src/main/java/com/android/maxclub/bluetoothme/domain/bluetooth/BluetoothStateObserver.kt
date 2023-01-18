package com.android.maxclub.bluetoothme.domain.bluetooth

import com.android.maxclub.bluetoothme.domain.bluetooth.model.BluetoothState
import kotlinx.coroutines.flow.Flow

interface BluetoothStateObserver {

    val initialState: BluetoothState

    fun getState(): Flow<BluetoothState>
}