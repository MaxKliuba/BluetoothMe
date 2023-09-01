package com.android.maxclub.bluetoothme.feature.main.presentation.main

import android.content.Intent
import com.android.maxclub.bluetoothme.feature.bluetooth.domain.bluetooth.models.BluetoothDevice
import com.android.maxclub.bluetoothme.feature.main.presentation.main.util.NavDrawerItem
import java.util.UUID

sealed class MainUiAction {
    class RequestMissingPermissions(vararg val permissions: String) : MainUiAction()
    object LaunchPermissionSettingsIntent : MainUiAction()
    data class LaunchBluetoothAdapterEnableIntent(val intent: Intent) : MainUiAction()
    object OpenNavigationDrawer : MainUiAction()
    data class NavigateToSelectedNavDrawerItem(val selectedItem: NavDrawerItem.Type) :
        MainUiAction()

    data class ShowConnectionErrorMessage(val device: BluetoothDevice) : MainUiAction()
    data class ShowSendingErrorMessage(val device: BluetoothDevice?) : MainUiAction()
    data class ShowMessagesCount(
        val inputMessagesCount: Int,
        val outputMessagesCount: Int,
    ) : MainUiAction()

    data class ShowWidgetDeletedMessage(val widgetId: UUID) : MainUiAction()
    data class ShowControllerDeletedMessage(val controllerId: UUID) : MainUiAction()
}
