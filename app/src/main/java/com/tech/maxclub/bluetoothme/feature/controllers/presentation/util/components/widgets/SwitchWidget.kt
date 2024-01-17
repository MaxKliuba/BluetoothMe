package com.tech.maxclub.bluetoothme.feature.controllers.presentation.util.components.widgets

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import com.tech.maxclub.bluetoothme.feature.controllers.domain.models.Widget
import com.tech.maxclub.bluetoothme.feature.controllers.presentation.util.components.AsIcon

@Composable
fun SwitchWidget(
    widget: Widget.Switch,
    onAction: (String, String) -> Unit,
    modifier: Modifier = Modifier,
    withTitlePadding: Boolean = false,
    overlay: @Composable BoxScope.() -> Unit,
) {
    BasicWidget(
        widgetTitle = widget.title,
        withTitlePadding = withTitlePadding,
        modifier = modifier,
        overlay = overlay,
    ) {
        val scaleValue = 1.7f

        Switch(
            checked = widget.state,
            onCheckedChange = {
                if (widget.enabled) {
                    onAction(widget.messageTag, widget.convertStateToMessageValue(it))
                }
            },
            thumbContent = { widget.icon.AsIcon(modifier = Modifier.scale(1f / scaleValue)) },
            modifier = Modifier.scale(scaleValue)
        )
    }
}