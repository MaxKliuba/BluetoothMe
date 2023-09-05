package com.android.maxclub.bluetoothme.feature.bluetooth.domain.usecases.bluetooth

import javax.inject.Inject

data class BluetoothUseCases @Inject constructor(
    val getState: GetBluetoothState,
    val getBluetoothDevices: GetBluetoothDevices,
    val updateBluetoothDevice: UpdateBluetoothDevice,
    val getFavoriteBluetoothDevice: GetFavoriteBluetoothDevice,
    val getScanState: GetScanState,
    val startScan: StartScan,
    val stopScan: StopScan,
    val enableAdapter: EnableAdapter,
    val connect: Connect,
    val disconnect: Disconnect,
)