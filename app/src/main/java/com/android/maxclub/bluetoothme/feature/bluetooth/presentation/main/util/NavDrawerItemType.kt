package com.android.maxclub.bluetoothme.feature.bluetooth.presentation.main.util

sealed class NavDrawerItemType(val value: String) {
    class Route(route: String) : NavDrawerItemType(route)
    class Url(url: String) : NavDrawerItemType(url)
}