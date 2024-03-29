package com.tech.maxclub.bluetoothme.feature.controllers.domain.models

enum class ControllerColumns(val count: Int) {
    TWO(2),
    THREE(3);

    fun next(limit: Int = WidgetSize.entries.size): ControllerColumns =
        entries[(ordinal + 1) % minOf(entries.size, limit)]
}