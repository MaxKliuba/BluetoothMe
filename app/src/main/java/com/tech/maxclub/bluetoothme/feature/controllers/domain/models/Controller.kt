package com.tech.maxclub.bluetoothme.feature.controllers.domain.models

data class Controller(
    val id: Int = 0,
    val title: String,
    val withAccelerometer: Boolean = false,
    val withVoiceInput: Boolean = false,
    val withRefresh: Boolean = false,
    val columnsCount: ControllerColumns = ControllerColumns.TWO,
    val position: Int = -1,
)