package com.tech.maxclub.bluetoothme.feature.controllers.domain.models

enum class WidgetSize(val span: Int) {
    SMALL(1),
    MIDDLE(2),
    LARGE(3);

    fun next(limit: Int = entries.size): WidgetSize =
        entries[(ordinal + 1) % minOf(entries.size, limit)]
}