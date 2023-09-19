package com.android.maxclub.bluetoothme.feature.controllers.domain.models.share

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ControllerJson(
    @SerialName("tl") val title: String,
    @SerialName("wa") val withAccelerometer: Boolean,
    @SerialName("wv") val withVoiceInput: Boolean,
    @SerialName("wr") val withRefresh: Boolean,
    @SerialName("ct") val columnsCount: Int,
)
