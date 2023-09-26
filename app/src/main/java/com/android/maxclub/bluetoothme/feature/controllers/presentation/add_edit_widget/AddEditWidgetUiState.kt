package com.android.maxclub.bluetoothme.feature.controllers.presentation.add_edit_widget

import androidx.compose.ui.text.input.TextFieldValue
import com.android.maxclub.bluetoothme.feature.controllers.domain.models.Widget

sealed class AddEditWidgetUiState {
    object Loading : AddEditWidgetUiState()
    data class Success(
        val widget: Widget<*>,
        val columnsCount: Int,
        val widgetTitle: TextFieldValue,
        val widgetTag: TextFieldValue,
        val rangeSliderPosition: IntRange,
        val stepSliderPosition: Int,
    ) : AddEditWidgetUiState()
}