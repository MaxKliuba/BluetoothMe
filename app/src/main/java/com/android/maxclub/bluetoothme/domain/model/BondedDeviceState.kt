package com.android.maxclub.bluetoothme.domain.model

sealed class BondedDeviceState {
    object Connected : BondedDeviceState()
    object Connecting : BondedDeviceState()
    object Disconnected : BondedDeviceState()
    object Disconnecting : BondedDeviceState()
}