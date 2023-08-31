package com.android.maxclub.bluetoothme.feature.controllers.presentation.add_edit_controller

import androidx.compose.ui.text.input.TextFieldValue
import com.android.maxclub.bluetoothme.feature.controllers.domain.models.Controller
import com.android.maxclub.bluetoothme.feature.controllers.domain.models.Widget
import java.util.UUID

sealed class AddEditControllerUiState {
    data class Loading(val controllerId: UUID) : AddEditControllerUiState()
    data class Success(
        val controllerTitle: TextFieldValue,
        val controller: Controller,
        val widgets: List<Widget>,
    ) : AddEditControllerUiState()
}