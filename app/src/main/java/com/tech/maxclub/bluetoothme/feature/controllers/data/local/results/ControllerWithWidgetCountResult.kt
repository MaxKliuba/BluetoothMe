package com.tech.maxclub.bluetoothme.feature.controllers.data.local.results

import androidx.room.Embedded
import com.tech.maxclub.bluetoothme.feature.controllers.data.local.entities.ControllerEntity

data class ControllerWithWidgetCountResult(
    @Embedded val controller: ControllerEntity,
    val widgetCount: Int,
)