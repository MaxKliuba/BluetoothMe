package com.android.maxclub.bluetoothme.feature.controllers.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.android.maxclub.bluetoothme.feature.controllers.domain.models.WidgetSize
import com.android.maxclub.bluetoothme.feature.controllers.domain.models.WidgetType
import java.util.UUID

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
    @PrimaryKey val id: UUID,
    val controllerId: UUID,
    val type: WidgetType,
    val messageTag: String,
    val title: String,
    val size: WidgetSize,
    val readOnly: Boolean,
    val position: Int,
    val isDeleted: Boolean = false,
)