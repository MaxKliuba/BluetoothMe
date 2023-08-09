package com.android.maxclub.bluetoothme.feature.bluetooth.presentation.main

import com.android.maxclub.bluetoothme.feature.bluetooth.domain.bluetooth.models.BluetoothDevice

sealed class MainUiEvent {
    object OnClickNavigationIcon : MainUiEvent()
    object OnOpenBluetoothPermissionRationaleDialog : MainUiEvent()
    object OnCloseBluetoothPermissionRationaleDialog : MainUiEvent()
    object OnEnableAdapter : MainUiEvent()
    data class OnConnect(val device: BluetoothDevice) : MainUiEvent()
    data class OnDisconnect(val device: BluetoothDevice?) : MainUiEvent()
    object OnClickMessagesCountBudge : MainUiEvent()
    data class OnNavigate(val route: String) : MainUiEvent()
    data class OnLaunchUrl(val url: String) : MainUiEvent()
}