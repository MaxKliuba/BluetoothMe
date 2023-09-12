package com.android.maxclub.bluetoothme.feature.controllers.data.mappers

import com.android.maxclub.bluetoothme.feature.controllers.data.local.entities.WidgetEntity
import com.android.maxclub.bluetoothme.feature.controllers.domain.models.Widget
import com.android.maxclub.bluetoothme.feature.controllers.domain.models.WidgetType

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
    }

fun Widget<*>.toWidgetEntity(): WidgetEntity =
    WidgetEntity(
        id = id,
        controllerId = controllerId,
        type = when (this) {
            is Widget.Empty -> WidgetType.EMPTY
            is Widget.Button -> WidgetType.BUTTON
            is Widget.Switch -> WidgetType.SWITCH
        },
        messageTag = messageTag,
        title = title,
        icon = icon,
        size = size,
        enabled = enabled,
        position = position,
        isDeleted = false,
    )