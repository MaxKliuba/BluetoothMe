package com.android.maxclub.bluetoothme.presentation.util

sealed class Screen(val route: String) {
    object ConnectionScreen : Screen("connection_screen")
}
