package com.android.maxclub.bluetoothme.presentation.util

sealed class Badge(val getValue: () -> String) {
    class Text(getValue: () -> String) : Badge(getValue)
    class Button(
        val onClick: () -> Unit,
        val isEnabled: Boolean = true,
        val withIndicator: Boolean = false,
        getValue: () -> String
    ) : Badge(getValue)
}
