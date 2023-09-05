package com.android.maxclub.bluetoothme.feature.controllers.presentation.controllers

import com.android.maxclub.bluetoothme.feature.controllers.domain.models.ControllerWithWidgetCount
import java.util.UUID

data class ControllersUiState(
    val isLoading: Boolean,
    val controllers: List<ControllerWithWidgetCount>,
    val selectedControllerId: UUID?,
    val isFabOpen: Boolean,
)