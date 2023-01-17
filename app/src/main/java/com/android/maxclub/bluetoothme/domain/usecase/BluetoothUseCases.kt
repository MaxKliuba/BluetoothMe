package com.android.maxclub.bluetoothme.domain.usecase

import javax.inject.Inject

data class BluetoothUseCases @Inject constructor(
    val getState: BluetoothStateUseCase,
    val getBondedDevices: GetBondedDevices,
    val connect: ConnectUseCase,
    val disconnect: DisconnectUseCase,
)
