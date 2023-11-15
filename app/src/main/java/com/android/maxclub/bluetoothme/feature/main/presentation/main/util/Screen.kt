package com.android.maxclub.bluetoothme.feature.main.presentation.main.util

sealed class Screen(val route: String) {
    object Connection : Screen("connection")

    object Controllers : Screen("controllers")

    object AddEditController : Screen("add_edit_controller") {
        const val ARG_CONTROLLER_ID = "controllerId" // optional
        const val DEFAULT_CONTROLLER_ID = 0

        val routeWithArgs: String
            get() = "$route?$ARG_CONTROLLER_ID={$ARG_CONTROLLER_ID}"
    }

    object AddEditWidget : Screen("add_edit_widget") {
        const val ARG_ID = "id"
        const val DEFAULT_ID = 0

        const val ARG_IS_NEW = "isNew"
        const val DEFAULT_IS_NEW = true

        const val ARG_COLUMNS_COUNT = "columnsCount"
        const val DEFAULT_COLUMNS_COUNT = 2

        val routeWithArgs: String
            get() = "$route/{$ARG_ID}/{$ARG_IS_NEW}/{$ARG_COLUMNS_COUNT}"
    }

    object ShareController : Screen("share_controller") {
        const val ARG_CONTROLLER_ID = "controllerId"
        const val DEFAULT_CONTROLLER_ID = 0

        val routeWithArgs: String
            get() = "$route/{$ARG_CONTROLLER_ID}"
    }

    object Controller : Screen("controller") {
        const val ARG_CONTROLLER_ID = "controllerId"
        const val DEFAULT_CONTROLLER_ID = -1

        val routeWithArgs: String
            get() = "$route/{$ARG_CONTROLLER_ID}"
    }

    object Chat : Screen("terminal")

    object QuickStart : Screen("https://github.com/BluetoothMe/QuickStart")
}
