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
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val controllerId: Int,
    val title: String,
    val position: Int,
    val isDeleted: Boolean = false,
)