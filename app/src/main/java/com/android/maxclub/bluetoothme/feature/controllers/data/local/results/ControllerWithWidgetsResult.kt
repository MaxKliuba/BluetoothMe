package com.android.maxclub.bluetoothme.feature.controllers.data.local.results

import androidx.room.Embedded
import androidx.room.Relation
import com.android.maxclub.bluetoothme.feature.controllers.data.local.entities.ControllerEntity
import com.android.maxclub.bluetoothme.feature.controllers.data.local.entities.WidgetEntity

data class ControllerWithWidgetsResult(
    @Embedded val controller: ControllerEntity,

    @Relation(
        entity = WidgetEntity::class,
        parentColumn = "id",
        entityColumn = "controllerId",
    )
    val widgets: List<WidgetEntity>,
)