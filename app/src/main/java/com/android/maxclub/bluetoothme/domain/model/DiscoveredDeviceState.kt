package com.android.maxclub.bluetoothme.domain.model

sealed class DiscoveredDeviceState {
    object Disconnected : DiscoveredDeviceState()
    object Bonding : DiscoveredDeviceState()
    object Bonded : DiscoveredDeviceState()
}