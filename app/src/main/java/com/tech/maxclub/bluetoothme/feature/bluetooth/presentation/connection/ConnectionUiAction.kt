package com.tech.maxclub.bluetoothme.feature.bluetooth.presentation.connection

import com.tech.maxclub.bluetoothme.feature.bluetooth.domain.bluetooth.models.BluetoothDevice

sealed class ConnectionUiAction {
    class RequestMissingBluetoothPermissions(vararg val permissions: String) : ConnectionUiAction()
    class ShowMissingLocationPermissionsDialog(vararg val permissions: String) :
        ConnectionUiAction()

    class RequestMissingLocationPermissions(vararg val permissions: String) : ConnectionUiAction()
    data class ShowDeviceType(val deviceType: String) : ConnectionUiAction()
    data class ScrollToConnectedDevice(val device: BluetoothDevice) : ConnectionUiAction()
}