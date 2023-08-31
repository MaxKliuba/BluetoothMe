package com.android.maxclub.bluetoothme.feature.controllers.data.mappers

import com.android.maxclub.bluetoothme.feature.controllers.data.local.entities.ControllerEntity
import com.android.maxclub.bluetoothme.feature.controllers.data.local.results.ControllerWithWidgetCountResult
import com.android.maxclub.bluetoothme.feature.controllers.data.local.results.ControllerWithWidgetsResult
import com.android.maxclub.bluetoothme.feature.controllers.domain.models.Controller
import com.android.maxclub.bluetoothme.feature.controllers.domain.models.ControllerWithWidgetCount
import com.android.maxclub.bluetoothme.feature.controllers.domain.models.ControllerWithWidgets

fun ControllerEntity.toController(): Controller =
    Controller(
        id = id,
        title = title,
        withAccelerometer = withAccelerometer,
        withVoiceInput = withVoiceInput,
        withRefresh = withRefresh,
        columnsCount = columnsCount,
        position = position,
    )

fun ControllerWithWidgetCountResult.toControllerWithWidgetCount(): ControllerWithWidgetCount =
    ControllerWithWidgetCount(
        controller = controller.toController(),
        widgetCount = widgetCount,
    )

fun ControllerWithWidgetsResult.toControllerWithWidgets(): ControllerWithWidgets =
    ControllerWithWidgets(
        controller = controller.toController(),
        widgets = widgets.filterNot { it.isDeleted }.map { it.toWidget() },
    )

fun Controller.toControllerEntity(): ControllerEntity =
    ControllerEntity(
        id = id,
        title = title,
        withAccelerometer = withAccelerometer,
        withVoiceInput = withVoiceInput,
        withRefresh = withRefresh,
        columnsCount = columnsCount,
        position = position,
    )