package com.android.maxclub.bluetoothme.feature_bluetooth.presentation.main.util

import androidx.navigation.NavController

fun NavController.smartNavigate(route: String) {
    if (currentDestination?.route != route) {
        if (containsBackStack(route)) {
            popBackStack(
                route = route,
                inclusive = false,
            )
        } else {
            navigate(route = route)
        }
    }
}

private fun NavController.containsBackStack(route: String): Boolean =
    try {
        getBackStackEntry(route)
        true
    } catch (e: IllegalArgumentException) {
        false
    }