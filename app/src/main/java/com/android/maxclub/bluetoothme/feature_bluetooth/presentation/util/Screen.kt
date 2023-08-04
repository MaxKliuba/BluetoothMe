package com.android.maxclub.bluetoothme.feature_bluetooth.presentation.util

sealed class Screen(val route: String) {
    object Connection : Screen("connection_screen")
    object Controllers : Screen("controllers_screen")
    object Terminal : Screen("terminal_screen")
    object Help : Screen("help_url")
}
