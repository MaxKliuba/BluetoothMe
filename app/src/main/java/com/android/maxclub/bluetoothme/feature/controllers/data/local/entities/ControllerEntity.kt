package com.android.maxclub.bluetoothme.feature.controllers.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "controllers")
data class ControllerEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val position: Int,
    val isDeleted: Boolean = false,
)