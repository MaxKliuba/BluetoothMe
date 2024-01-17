package com.tech.maxclub.bluetoothme.feature.main.presentation.main

import android.content.Intent
import com.tech.maxclub.bluetoothme.feature.bluetooth.domain.bluetooth.models.BluetoothDevice

sealed class MainUiAction {
    class RequestMissingBluetoothPermissions(vararg val permissions: String) : MainUiAction()
    data class LaunchBluetoothAdapterEnableIntent(val intent: Intent) : MainUiAction()
    data class ShowConnectionErrorMessage(val device: BluetoothDevice) : MainUiAction()
    data class ShowMessagesCountMessage(
        val inputMessagesCount: Int,
        val outputMessagesCount: Int,
    ) : MainUiAction()

    data class ShowSendingErrorMessage(val device: BluetoothDevice?) : MainUiAction()
    data class ShowWidgetDeletedMessage(val widgetId: Int) : MainUiAction()
    data class ShowControllerDeletedMessage(val controllerId: Int) : MainUiAction()
}
