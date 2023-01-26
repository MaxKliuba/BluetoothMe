package com.android.maxclub.bluetoothme.feature_bluetooth.presentation.main

import com.android.maxclub.bluetoothme.feature_bluetooth.domain.bluetooth.models.BluetoothDevice

sealed class MainEvent {
    object OnClickNavigationIcon : MainEvent()
    object OnEnableAdapter : MainEvent()
    data class OnConnect(val device: BluetoothDevice) : MainEvent()
    data class OnDisconnect(val device: BluetoothDevice?) : MainEvent()
    object OnClickMessagesCountBudge : MainEvent()
    data class OnNavigate(val route: String) : MainEvent()
    data class OnLaunchUrl(val url: String) : MainEvent()
}
