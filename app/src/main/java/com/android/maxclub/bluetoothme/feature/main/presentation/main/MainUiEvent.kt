package com.android.maxclub.bluetoothme.feature.main.presentation.main

import com.android.maxclub.bluetoothme.feature.bluetooth.domain.bluetooth.models.BluetoothDevice
import com.android.maxclub.bluetoothme.feature.main.presentation.main.util.NavDrawerItem

sealed class MainUiEvent {
    object OnOpenNavigationDrawer : MainUiEvent()
    data class OnSelectNavDrawerItem(val selectedItem: NavDrawerItem.Type) : MainUiEvent()
    data class OnDestinationChanged(val route: String) : MainUiEvent()
    class OnRequestMissingPermissions(vararg val permissions: String) : MainUiEvent()
    object OnShowBluetoothPermissionRationaleDialog : MainUiEvent()
    object OnConfirmBluetoothPermissionRationaleDialog : MainUiEvent()
    object OnDismissBluetoothPermissionRationaleDialog : MainUiEvent()
    object OnEnableAdapter : MainUiEvent()
    data class OnConnect(val device: BluetoothDevice) : MainUiEvent()
    data class OnDisconnect(val device: BluetoothDevice?) : MainUiEvent()
    object OnShowMessagesCount : MainUiEvent()
    object OnShowSendingErrorMessage : MainUiEvent()
}