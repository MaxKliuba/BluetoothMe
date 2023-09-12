package com.android.maxclub.bluetoothme.feature.main.presentation.main.util

sealed class Screen(val route: String) {
    object Connection : Screen("connection")
    object Controllers : Screen("controllers")
    object AddEditController : Screen("add_edit_controller") {
        const val ARG_CONTROLLER_ID = "controllerId" // optional
        const val DEFAULT_CONTROLLER_ID = -1

        val routeWithArgs: String
            get() = "$route?$ARG_CONTROLLER_ID={$ARG_CONTROLLER_ID}"
    }

    object AddEditWidget : Screen("add_edit_widget") {
        const val ARG_ID = "id"
        const val DEFAULT_ID = -1

        const val ARG_IS_NEW = "isNew"
        const val DEFAULT_IS_NEW = true

        val routeWithArgs: String
            get() = "$route/{$ARG_ID}/{$ARG_IS_NEW}"
    }

    object Controller : Screen("controller") {
        const val ARG_CONTROLLER_ID = "controllerId"
        const val DEFAULT_CONTROLLER_ID = -1

        val routeWithArgs: String
            get() = "$route/{$ARG_CONTROLLER_ID}"
    }

    object Chat : Screen("terminal")
    object Help : Screen("help")
}
