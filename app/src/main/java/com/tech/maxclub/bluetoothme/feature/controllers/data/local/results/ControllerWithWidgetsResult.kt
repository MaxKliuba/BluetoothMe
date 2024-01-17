package com.tech.maxclub.bluetoothme.feature.controllers.data.local.results

import androidx.room.Embedded
import androidx.room.Relation
import com.tech.maxclub.bluetoothme.feature.controllers.data.local.entities.ControllerEntity
import com.tech.maxclub.bluetoothme.feature.controllers.data.local.entities.WidgetEntity

data class ControllerWithWidgetsResult(
    @Embedded val controller: ControllerEntity,

    @Relation(
        entity = WidgetEntity::class,
        parentColumn = "id",
        entityColumn = "controllerId",
    )
    val widgets: List<WidgetEntity>,
)