package com.tech.maxclub.bluetoothme.feature.controllers.domain.models

import androidx.annotation.StringRes
import com.tech.maxclub.bluetoothme.R
import kotlin.reflect.KClass

enum class WidgetType(@StringRes val titleResId: Int) {
    EMPTY(R.string.widget_type_empty),
    BUTTON(R.string.widget_type_button),
    SWITCH(R.string.widget_type_switch),
    SLIDER(R.string.widget_type_slider),
    TEXT(R.string.widget_type_text),
    // Append new here
}

@Suppress("UNCHECKED_CAST")
inline fun <reified T : Widget<*>> WidgetType.toWidgetClass(): KClass<T> =
    when (this) {
        WidgetType.EMPTY -> Widget.Empty::class as KClass<T>
        WidgetType.BUTTON -> Widget.Button::class as KClass<T>
        WidgetType.SWITCH -> Widget.Switch::class as KClass<T>
        WidgetType.SLIDER -> Widget.Slider::class as KClass<T>
        WidgetType.TEXT -> Widget.Text::class as KClass<T>
    }

fun Widget<*>.toWidgetType() =
    when (this) {
        is Widget.Empty -> WidgetType.EMPTY
        is Widget.Button -> WidgetType.BUTTON
        is Widget.Switch -> WidgetType.SWITCH
        is Widget.Slider -> WidgetType.SLIDER
        is Widget.Text -> WidgetType.TEXT
    }