package com.android.maxclub.bluetoothme.presentation.main

import com.android.maxclub.bluetoothme.domain.bluetooth.model.BluetoothDevice

sealed class MainEvent {
    object OnClickNavigationIcon : MainEvent()
    object OnEnableAdapter : MainEvent()
    data class OnConnect(val device: BluetoothDevice) : MainEvent()
    data class OnDisconnect(val device: BluetoothDevice?) : MainEvent()
    object OnClickMessagesCountBudge : MainEvent()
    data class OnNavigate(val route: String) : MainEvent()
    data class OnLaunchUrl(val url: String) : MainEvent()
}
