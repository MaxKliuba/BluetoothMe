package com.android.maxclub.bluetoothme.feature_bluetooth.presentation.main.util

sealed class NavDrawerBadge(val getValue: () -> String) {
    class Text(getValue: () -> String) : NavDrawerBadge(getValue)
    class Button(
        val onClick: () -> Unit,
        val isEnabled: Boolean = true,
        val withIndicator: Boolean = false,
        getValue: () -> String
    ) : NavDrawerBadge(getValue)
}