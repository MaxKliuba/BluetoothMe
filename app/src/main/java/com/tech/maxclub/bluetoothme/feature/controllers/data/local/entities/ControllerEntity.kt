package com.tech.maxclub.bluetoothme.feature.controllers.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tech.maxclub.bluetoothme.feature.controllers.domain.models.ControllerColumns

@Entity(tableName = "controllers")
data class ControllerEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val withAccelerometer: Boolean,
    val withVoiceInput: Boolean,
    val withRefresh: Boolean,
    val columnsCount: ControllerColumns,
    val position: Int = -1,
    val isDeleted: Boolean = false,
)