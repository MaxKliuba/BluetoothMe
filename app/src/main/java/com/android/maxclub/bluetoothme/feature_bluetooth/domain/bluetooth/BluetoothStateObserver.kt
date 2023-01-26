package com.android.maxclub.bluetoothme.feature_bluetooth.domain.bluetooth

import com.android.maxclub.bluetoothme.feature_bluetooth.domain.bluetooth.models.BluetoothState
import kotlinx.coroutines.flow.Flow

interface BluetoothStateObserver {

    val initialState: BluetoothState

    fun getState(): Flow<BluetoothState>
}