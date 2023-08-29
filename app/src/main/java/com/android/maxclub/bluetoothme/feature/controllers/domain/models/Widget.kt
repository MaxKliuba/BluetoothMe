package com.android.maxclub.bluetoothme.feature.controllers.domain.models

import java.util.UUID

data class Widget(
    val id: UUID = UUID.randomUUID(),
    val controllerId: UUID,
    val title: String,
    val position: Int = -1,
)