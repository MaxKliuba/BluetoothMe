package com.android.maxclub.bluetoothme.feature.controllers.presentation.controller

import com.android.maxclub.bluetoothme.feature.controllers.domain.models.Controller
import com.android.maxclub.bluetoothme.feature.controllers.domain.models.Widget

sealed class ControllerUiState {
    object Loading : ControllerUiState()
    data class Success(
        val controller: Controller,
        val widgets: List<Widget<*>>,
    ) : ControllerUiState()
}
