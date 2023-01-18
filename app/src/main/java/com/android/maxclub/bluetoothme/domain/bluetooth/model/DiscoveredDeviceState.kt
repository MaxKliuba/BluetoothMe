package com.android.maxclub.bluetoothme.domain.bluetooth.model

sealed class DiscoveredDeviceState {
    object Disconnected : DiscoveredDeviceState()
    object Bonding : DiscoveredDeviceState()
    object Bonded : DiscoveredDeviceState()
}