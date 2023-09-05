package com.android.maxclub.bluetoothme.feature.controllers.domain.models

import java.util.UUID

data class Controller(
    val id: UUID = UUID.randomUUID(),
    val title: String,
    val withAccelerometer: Boolean = false,
    val withVoiceInput: Boolean = false,
    val withRefresh: Boolean = false,
    val columnsCount: ControllerColumns = ControllerColumns.TWO,
    val position: Int = -1,
)