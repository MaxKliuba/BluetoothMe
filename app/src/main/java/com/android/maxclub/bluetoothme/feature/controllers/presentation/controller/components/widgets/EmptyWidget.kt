package com.android.maxclub.bluetoothme.feature.controllers.presentation.controller.components.widgets

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.android.maxclub.bluetoothme.feature.controllers.domain.models.Widget

@Composable
fun EmptyWidget(
    widget: Widget.Empty,
    modifier: Modifier = Modifier
) {
    BasicWidget(
        widget = widget,
        modifier = modifier,
    ) {
        // Empty
    }
}