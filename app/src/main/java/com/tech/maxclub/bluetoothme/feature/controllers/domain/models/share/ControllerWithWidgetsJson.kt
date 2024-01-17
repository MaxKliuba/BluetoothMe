package com.tech.maxclub.bluetoothme.feature.controllers.domain.models.share

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ControllerWithWidgetsJson(
    @SerialName("ctr") val controller: ControllerJson,
    @SerialName("wds") val widgets: List<WidgetJson>,
)