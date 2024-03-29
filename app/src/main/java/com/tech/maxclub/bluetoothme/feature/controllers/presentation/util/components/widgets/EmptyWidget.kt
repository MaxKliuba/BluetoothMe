package com.tech.maxclub.bluetoothme.feature.controllers.presentation.util.components.widgets

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.tech.maxclub.bluetoothme.feature.controllers.domain.models.Widget

@Composable
fun EmptyWidget(
    widget: Widget.Empty,
    modifier: Modifier = Modifier,
    overlay: @Composable BoxScope.() -> Unit,
) {
    BasicWidget(
        widgetTitle = widget.title,
        withTitlePadding = false,
        modifier = modifier,
        overlay = overlay,
    ) {
        // Empty
    }
}