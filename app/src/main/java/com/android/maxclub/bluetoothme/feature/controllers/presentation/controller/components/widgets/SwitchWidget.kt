package com.android.maxclub.bluetoothme.feature.controllers.presentation.controller.components.widgets

import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import com.android.maxclub.bluetoothme.feature.controllers.domain.models.Widget
import com.android.maxclub.bluetoothme.feature.controllers.presentation.util.AsIcon

@Composable
fun SwitchWidget(
    widget: Widget.Switch,
    onAction: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    val scaleValue = 2f

    BasicWidget(
        widget = widget,
        modifier = modifier,
    ) {
        Switch(
            checked = widget.state,
            onCheckedChange = { onAction(widget.messageTag, widget.convertStateToMessageValue(it)) },
            enabled = widget.enabled,
            thumbContent = {
                widget.icon.AsIcon(modifier = Modifier.scale(1f / scaleValue))
            },
            modifier = Modifier.scale(scaleValue)
        )
    }
}