package com.android.maxclub.bluetoothme.domain.usecase.bluetooth

import javax.inject.Inject

data class BluetoothUseCases @Inject constructor(
    val getState: GetBluetoothState,
    val getBluetoothDevices: GetBluetoothDevices,
    val startScan: StartScan,
    val stopScan: StopScan,
    val connect: Connect,
    val disconnect: Disconnect,
)
