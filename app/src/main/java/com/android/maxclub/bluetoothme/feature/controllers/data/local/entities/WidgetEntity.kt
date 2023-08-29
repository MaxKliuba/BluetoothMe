package com.android.maxclub.bluetoothme.feature.controllers.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

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
    @PrimaryKey val id: String,
    val controllerId: String,
    val title: String,
    val position: Int,
    val isDeleted: Boolean = false,
)