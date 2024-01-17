package com.tech.maxclub.bluetoothme.feature.main.presentation.main.util

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class NavDrawerItem(
    val type: Type,
    @DrawableRes val icon: Int,
    @StringRes val label: Int,
    val badge: Badge?,
) {
    sealed class Type(val value: String) {
        class Route(route: String) : Type(route)
        class Url(url: String) : Type(url)
    }

    sealed class Badge(val getValue: () -> String) {
        class Text(getValue: () -> String) : Badge(getValue)
        class Button(
            val onClick: () -> Unit,
            val isEnabled: Boolean = true,
            val isProgressIndicatorVisible: Boolean = false,
            getValue: () -> String
        ) : Badge(getValue)
    }
}