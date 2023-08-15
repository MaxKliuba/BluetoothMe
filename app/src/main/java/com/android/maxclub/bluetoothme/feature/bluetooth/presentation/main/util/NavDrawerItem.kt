package com.android.maxclub.bluetoothme.feature.bluetooth.presentation.main.util

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class NavDrawerItem(
    val type: NavDrawerItemType,
    @DrawableRes val icon: Int,
    @StringRes val label: Int,
    val badge: NavDrawerBadge?,
)