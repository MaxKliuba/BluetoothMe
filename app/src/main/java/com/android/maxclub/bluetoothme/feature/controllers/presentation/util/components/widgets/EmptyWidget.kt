package com.android.maxclub.bluetoothme.feature.controllers.presentation.util.components.widgets

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.android.maxclub.bluetoothme.feature.controllers.domain.models.Widget

@Composable
fun EmptyWidget(
    widget: Widget.Empty,
    modifier: Modifier = Modifier,
    overlay: @Composable BoxScope.() -> Unit,
) {
    BasicWidget(
        widgetTitle = widget.title,
        modifier = modifier,
        overlay = overlay,
    ) {
        // Empty
    }
}