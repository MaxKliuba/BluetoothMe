package com.android.maxclub.bluetoothme.feature.controllers.data.mappers

import com.android.maxclub.bluetoothme.feature.controllers.data.local.entities.WidgetEntity
import com.android.maxclub.bluetoothme.feature.controllers.domain.models.Widget
import com.android.maxclub.bluetoothme.feature.controllers.domain.models.WidgetType
import com.android.maxclub.bluetoothme.feature.controllers.domain.models.toWidgetType

fun WidgetEntity.toWidget(): Widget<*> =
    when (type) {
        WidgetType.EMPTY -> Widget.Empty(
            id = id,
            controllerId = controllerId,
            size = size,
            position = position,
        )

        WidgetType.BUTTON -> Widget.Button(
            id = id,
            controllerId = controllerId,
            messageTag = messageTag,
            title = title,
            icon = icon,
            size = size,
            enabled = enabled,
            position = position,
        )

        WidgetType.SWITCH -> Widget.Switch(
            id = id,
            controllerId = controllerId,
            messageTag = messageTag,
            title = title,
            icon = icon,
            size = size,
            enabled = enabled,
            position = position,
        )

        WidgetType.SLIDER -> Widget.Slider(
            id = id,
            controllerId = controllerId,
            messageTag = messageTag,
            title = title,
            icon = icon,
            size = size,
            enabled = enabled,
            position = position,
            minValue = minValue ?: Widget.Slider.DEFAULT_MIN_VALUE,
            maxValue = maxValue ?: Widget.Slider.DEFAULT_MAX_VALUE,
            step = step ?: Widget.Slider.DEFAULT_STEP,
        )

        WidgetType.TEXT -> Widget.Text(
            id = id,
            controllerId = controllerId,
            messageTag = messageTag,
            title = title,
            icon = icon,
            size = size,
            enabled = enabled,
            position = position,
        )
    }

fun Widget<*>.toWidgetEntity(): WidgetEntity =
    WidgetEntity(
        id = id,
        controllerId = controllerId,
        type = this.toWidgetType(),
        messageTag = messageTag,
        title = title,
        icon = icon,
        size = size,
        enabled = enabled,
        position = position,
        isDeleted = false,
        minValue = (this as? Widget.Slider)?.minValue,
        maxValue = (this as? Widget.Slider)?.maxValue,
        step = (this as? Widget.Slider)?.step,
    )