package com.android.maxclub.bluetoothme.feature_bluetooth.presentation.util

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class NavigationDrawerItem(
    val route: String,
    @DrawableRes val icon: Int,
    @StringRes val label: Int,
    val badge: Badge?,
    val onClick: (String) -> Unit,
)