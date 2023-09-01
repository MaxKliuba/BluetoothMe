package com.android.maxclub.bluetoothme.feature.controllers.presentation.add_edit_widget

import com.android.maxclub.bluetoothme.feature.controllers.domain.models.Widget

sealed class AddEditWidgetUiState {
    object Loading : AddEditWidgetUiState()
    data class Success(
        // TODO
        val widget: Widget,
    ) : AddEditWidgetUiState()
}