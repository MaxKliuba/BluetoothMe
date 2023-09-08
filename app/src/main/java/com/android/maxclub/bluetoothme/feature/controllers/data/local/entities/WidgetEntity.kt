package com.android.maxclub.bluetoothme.feature.controllers.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.android.maxclub.bluetoothme.feature.controllers.domain.models.WidgetSize
import com.android.maxclub.bluetoothme.feature.controllers.domain.models.WidgetType

@Entity(
    tableName = "widgets",
    foreignKeys = [
        ForeignKey(
            entity = ControllerEntity::class,
            parentColumns = ["id"],
            childColumns = ["controllerId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ]
)
data class WidgetEntity(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val controllerId: Int,
    val type: WidgetType,
    val messageTag: String,
    val title: String,
    val size: WidgetSize,
    val enabled: Boolean,
    val position: Int,
    val isDeleted: Boolean = false,
)