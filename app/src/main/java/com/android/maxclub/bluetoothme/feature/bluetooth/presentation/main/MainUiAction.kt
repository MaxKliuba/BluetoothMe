package com.android.maxclub.bluetoothme.feature.bluetooth.presentation.main

import android.content.Intent
import com.android.maxclub.bluetoothme.feature.bluetooth.domain.bluetooth.models.BluetoothDevice

sealed class MainUiAction {
    class RequestMissingPermissions(vararg val permissions: String) : MainUiAction()
    data class LaunchIntent(val intent: Intent) : MainUiAction()
    data class ShowConnectionErrorMessage(val device: BluetoothDevice) : MainUiAction()
    data class ShowMessagesCount(
        val inputMessagesCount: Int,
        val outputMessagesCount: Int,
    ) : MainUiAction()
}
