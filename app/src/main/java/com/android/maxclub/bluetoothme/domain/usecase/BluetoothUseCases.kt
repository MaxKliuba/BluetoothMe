package com.android.maxclub.bluetoothme.domain.usecase

import javax.inject.Inject

data class BluetoothUseCases @Inject constructor(
    val getBondedDevices: GetBondedDevices,
    val connectUseCase: ConnectUseCase,
    val disconnectUseCase: DisconnectUseCase,
)
