package com.tech.maxclub.bluetoothme.feature.controllers.domain.models.share

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WidgetJson(
    @SerialName("tp") val type: Int,
    @SerialName("tg") val messageTag: String,
    @SerialName("tl") val title: String,
    @SerialName("ic") val icon: Int,
    @SerialName("sz") val size: Int,
    @SerialName("en") val enabled: Boolean,
    @SerialName("ps") val position: Int,
    @SerialName("mn") val minValue: Int,
    @SerialName("mx") val maxValue: Int,
    @SerialName("st") val step: Int,
)
