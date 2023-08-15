package com.android.maxclub.bluetoothme.feature.bluetooth.presentation.connection

import com.android.maxclub.bluetoothme.feature.bluetooth.domain.bluetooth.models.BluetoothDevice

sealed class ConnectionUiAction {
    class RequestMissingPermissions(vararg val permissions: String) : ConnectionUiAction()
    object LaunchBluetoothSettingsIntent : ConnectionUiAction()
    data class ShowDeviceType(val deviceType: String) : ConnectionUiAction()
    data class ScrollToConnectedDevice(val device: BluetoothDevice) : ConnectionUiAction()
}