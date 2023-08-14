package com.android.maxclub.bluetoothme.feature.bluetooth.presentation.main

import com.android.maxclub.bluetoothme.feature.bluetooth.domain.bluetooth.models.BluetoothDevice

sealed class MainUiEvent {
    data class OnDestinationChanged(val route: String) : MainUiEvent()
    object OnOpenBluetoothPermissionRationaleDialog : MainUiEvent()
    object OnCloseBluetoothPermissionRationaleDialog : MainUiEvent()
    object OnEnableAdapter : MainUiEvent()
    data class OnConnect(val device: BluetoothDevice) : MainUiEvent()
    data class OnDisconnect(val device: BluetoothDevice?) : MainUiEvent()
    object OnClickMessagesCountBudge : MainUiEvent()
}