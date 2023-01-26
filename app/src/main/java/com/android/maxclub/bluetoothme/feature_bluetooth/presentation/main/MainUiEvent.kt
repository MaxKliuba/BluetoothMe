package com.android.maxclub.bluetoothme.feature_bluetooth.presentation.main

import android.content.Intent
import com.android.maxclub.bluetoothme.feature_bluetooth.domain.bluetooth.models.BluetoothDevice

sealed class MainUiEvent {
    class OnShowMissingPermissionMessage(vararg val permissions: String) : MainUiEvent()
    data class OnLaunchIntent(val intent: Intent) : MainUiEvent()
    data class OnShowConnectionErrorMessage(val device: BluetoothDevice) : MainUiEvent()
    object OnOpenNavigationDrawer : MainUiEvent()
    data class OnShowMessagesCount(
        val inputMessagesCount: Int,
        val outputMessagesCount: Int,
    ) : MainUiEvent()

    data class OnNavigate(val route: String) : MainUiEvent()
    data class OnLaunchUrl(val url: String) : MainUiEvent()
}