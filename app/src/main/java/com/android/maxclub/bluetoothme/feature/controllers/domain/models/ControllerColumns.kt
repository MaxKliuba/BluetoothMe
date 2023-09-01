package com.android.maxclub.bluetoothme.feature.controllers.domain.models

enum class ControllerColumns(val count: Int) {
    TWO(2),
    THREE(3);

    fun next(limit: Int = WidgetSize.values().size): ControllerColumns =
        values()[(ordinal + 1) % minOf(values().size, limit)]
}