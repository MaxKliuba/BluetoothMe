package com.android.maxclub.bluetoothme.feature.bluetooth.presentation.main

import android.content.Intent
import com.android.maxclub.bluetoothme.feature.bluetooth.domain.bluetooth.models.BluetoothDevice
import com.android.maxclub.bluetoothme.feature.bluetooth.presentation.main.util.NavDrawerItemType

sealed class MainUiAction {
    class RequestMissingPermissions(vararg val permissions: String) : MainUiAction()
    object LaunchPermissionSettingsIntent : MainUiAction()
    data class LaunchBluetoothAdapterEnableIntent(val intent: Intent) : MainUiAction()
    object OpenNavigationDrawer : MainUiAction()
    data class NavigateToSelectedNavDrawerItem(val selectedItem: NavDrawerItemType) : MainUiAction()
    data class ShowConnectionErrorMessage(val device: BluetoothDevice) : MainUiAction()
    data class ShowSendingErrorMessage(val device: BluetoothDevice?) : MainUiAction()
    data class ShowMessagesCount(
        val inputMessagesCount: Int,
        val outputMessagesCount: Int,
    ) : MainUiAction()
}
