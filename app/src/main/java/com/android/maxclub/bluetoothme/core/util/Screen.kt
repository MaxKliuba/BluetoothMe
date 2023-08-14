package com.android.maxclub.bluetoothme.core.util

sealed class Screen(val route: String) {
    object Connection : Screen("connection_screen")
    object Controllers : Screen("controllers_screen")
    object Chat : Screen("terminal_screen")
    object Help : Screen("help_url")
}
