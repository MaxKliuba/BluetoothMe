package com.tech.maxclub.bluetoothme.feature.controllers.presentation.controllers

import com.tech.maxclub.bluetoothme.feature.controllers.domain.models.ControllerWithWidgetCount

data class ControllersUiState(
    val isLoading: Boolean,
    val controllers: List<ControllerWithWidgetCount>,
    val selectedControllerId: Int?,
    val isFabOpen: Boolean,
    val isCameraPermissionRationaleDialogVisible: Boolean,
)