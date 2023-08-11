package com.android.maxclub.bluetoothme.feature.bluetooth.presentation.connection

import com.android.maxclub.bluetoothme.feature.bluetooth.domain.bluetooth.models.BluetoothDevice
import com.android.maxclub.bluetoothme.feature.bluetooth.presentation.connection.util.BleProfileDialogData

sealed class ConnectionUiEvent {
    object OnStartScan : ConnectionUiEvent()
    object OnStopScan : ConnectionUiEvent()
    object OnOpenBluetoothSettings : ConnectionUiEvent()
    data class OnClickDeviceIcon(val deviceType: String) : ConnectionUiEvent()
    data class OnUpdateBluetoothDevice(val device: BluetoothDevice) : ConnectionUiEvent()
    data class OnChangeBleProfileData(val data: BleProfileDialogData) : ConnectionUiEvent()
    data class OnOpenBleProfileDialog(val data: BleProfileDialogData) : ConnectionUiEvent()
    data class OnConfirmBleProfileDialog(val data: BleProfileDialogData) : ConnectionUiEvent()
    object OnDismissBleProfileDialog : ConnectionUiEvent()
}