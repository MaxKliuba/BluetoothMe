package com.tech.maxclub.bluetoothme.feature.controllers.presentation.add_edit_controller

sealed class AddEditControllerUiAction {
    data class SetFocusToTitleTextField(val hideKeyboard: Boolean) : AddEditControllerUiAction()
}