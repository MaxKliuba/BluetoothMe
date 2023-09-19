package com.android.maxclub.bluetoothme.feature.controllers.data.mappers

import com.android.maxclub.bluetoothme.feature.controllers.data.local.Converters
import com.android.maxclub.bluetoothme.feature.controllers.data.local.entities.WidgetEntity
import com.android.maxclub.bluetoothme.feature.controllers.domain.models.share.WidgetJson
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

fun WidgetEntity.toWidgetJson(): WidgetJson =
    WidgetJson(
        type = type.ordinal,
        messageTag = messageTag,
        title = title,
        icon = icon.id,
        size = size.span,
        enabled = enabled,
        position = position,
        minValue = minValue ?: 0,
        maxValue = maxValue ?: 0,
        step = step ?: 0,
    )

fun WidgetJson.toWidgetEntity(controllerId: Int): WidgetEntity =
    WidgetEntity(
        controllerId = controllerId,
        type = Converters().toWidgetType(type),
        messageTag = messageTag,
        title = title,
        icon = Converters().toWidgetIcon(icon),
        size = Converters().toWidgetSize(size),
        enabled = enabled,
        position = position,
        minValue = minValue,
        maxValue = maxValue,
        step = step,
    )