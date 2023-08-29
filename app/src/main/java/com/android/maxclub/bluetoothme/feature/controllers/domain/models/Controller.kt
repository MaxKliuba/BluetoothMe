package com.android.maxclub.bluetoothme.feature.controllers.domain.models

import java.util.UUID

data class Controller(
    val id: UUID = UUID.randomUUID(),
    val title: String,
    val withAccelerometer: Boolean,
    val withVoiceInput: Boolean,
    val withRefresh: Boolean,
    val position: Int = -1,
)