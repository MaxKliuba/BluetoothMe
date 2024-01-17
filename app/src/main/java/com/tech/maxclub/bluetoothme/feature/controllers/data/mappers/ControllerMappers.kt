package com.tech.maxclub.bluetoothme.feature.controllers.data.mappers

import com.tech.maxclub.bluetoothme.feature.controllers.data.local.Converters
import com.tech.maxclub.bluetoothme.feature.controllers.data.local.entities.ControllerEntity
import com.tech.maxclub.bluetoothme.feature.controllers.data.local.results.ControllerWithWidgetCountResult
import com.tech.maxclub.bluetoothme.feature.controllers.data.local.results.ControllerWithWidgetsResult
import com.tech.maxclub.bluetoothme.feature.controllers.domain.models.share.ControllerJson
import com.tech.maxclub.bluetoothme.feature.controllers.domain.models.share.ControllerWithWidgetsJson
import com.tech.maxclub.bluetoothme.feature.controllers.domain.models.Controller
import com.tech.maxclub.bluetoothme.feature.controllers.domain.models.ControllerWithWidgetCount
import com.tech.maxclub.bluetoothme.feature.controllers.domain.models.ControllerWithWidgets

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

fun ControllerEntity.toControllerJson(): ControllerJson =
    ControllerJson(
        title = title,
        withAccelerometer = withAccelerometer,
        withVoiceInput = withVoiceInput,
        withRefresh = withRefresh,
        columnsCount = columnsCount.count,
    )

fun ControllerWithWidgetsResult.toControllerWithWidgetsJson(): ControllerWithWidgetsJson =
    ControllerWithWidgetsJson(
        controller = controller.toControllerJson(),
        widgets = widgets.filterNot { it.isDeleted }.map { it.toWidgetJson() },
    )

fun ControllerJson.toControllerEntity(): ControllerEntity =
    ControllerEntity(
        title = title,
        withAccelerometer = withAccelerometer,
        withVoiceInput = withVoiceInput,
        withRefresh = withRefresh,
        columnsCount = Converters().toControllerColumns(columnsCount),
    )