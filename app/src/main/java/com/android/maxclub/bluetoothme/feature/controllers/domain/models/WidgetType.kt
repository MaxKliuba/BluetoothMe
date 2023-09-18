package com.android.maxclub.bluetoothme.feature.controllers.domain.models

import kotlin.reflect.KClass

enum class WidgetType {
    EMPTY,
    BUTTON,
    SWITCH,
    SLIDER,
    // Append new here
}

@Suppress("UNCHECKED_CAST")
inline fun <reified T : Widget<*>> WidgetType.toWidgetClass(): KClass<T> =
    when (this) {
        WidgetType.EMPTY -> Widget.Empty::class as KClass<T>
        WidgetType.BUTTON -> Widget.Button::class as KClass<T>
        WidgetType.SWITCH -> Widget.Switch::class as KClass<T>
        WidgetType.SLIDER -> Widget.Slider::class as KClass<T>
    }

fun Widget<*>.toWidgetType() =
    when (this) {
        is Widget.Empty -> WidgetType.EMPTY
        is Widget.Button -> WidgetType.BUTTON
        is Widget.Switch -> WidgetType.SWITCH
        is Widget.Slider -> WidgetType.SLIDER
    }