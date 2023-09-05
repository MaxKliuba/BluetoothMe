package com.android.maxclub.bluetoothme.core.util

sealed class Screen(val route: String) {
    object Connection : Screen("connection")
    object Controllers : Screen("controllers")
    object AddEditController : Screen("add_edit_controller") {
        const val ARG_CONTROLLER_ID = "controllerId" // optional

        val routeWithArgs: String
            get() = "$route?$ARG_CONTROLLER_ID={$ARG_CONTROLLER_ID}"
    }

    object AddEditWidget : Screen("add_edit_widget") {
        const val ARG_ID = "id"
        const val ARG_IS_NEW = "isNew"

        val routeWithArgs: String
            get() = "$route/{$ARG_ID}/{$ARG_IS_NEW}"
    }

    object Controller : Screen("controller")
    object Chat : Screen("terminal")
    object Help : Screen("help")
}