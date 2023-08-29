package com.android.maxclub.bluetoothme.feature.controllers.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "controllers")
data class ControllerEntity(
    @PrimaryKey val id: String,
    val title: String,
    val withAccelerometer: Boolean,
    val withVoiceInput: Boolean,
    val withRefresh: Boolean,
    val position: Int,
    val isDeleted: Boolean = false,
)