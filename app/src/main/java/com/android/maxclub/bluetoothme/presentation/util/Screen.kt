package com.android.maxclub.bluetoothme.presentation.util

sealed class Screen(val route: String) {
    object ConnectionScreen : Screen("connection_screen")
    object ControllersScreen : Screen("controllers_screen")
    object TerminalScreen : Screen("terminal_screen")
}
