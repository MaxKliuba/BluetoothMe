package com.android.maxclub.bluetoothme.feature.controllers.data.mappers

import com.android.maxclub.bluetoothme.feature.controllers.data.local.entities.WidgetEntity
import com.android.maxclub.bluetoothme.feature.controllers.domain.models.Widget
import com.android.maxclub.bluetoothme.feature.controllers.domain.models.WidgetType

fun WidgetEntity.toWidget(): Widget =
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
            size = size,
            readOnly = readOnly,
            position = position,
        )
    }

fun Widget.toWidgetEntity(): WidgetEntity =
    WidgetEntity(
        id = id,
        controllerId = controllerId,
        type = when (this) {
            is Widget.Empty -> WidgetType.EMPTY
            is Widget.Button -> WidgetType.BUTTON
        },
        messageTag = messageTag,
        title = title,
        size = size,
        readOnly = readOnly,
        position = position,
        isDeleted = false,
    )