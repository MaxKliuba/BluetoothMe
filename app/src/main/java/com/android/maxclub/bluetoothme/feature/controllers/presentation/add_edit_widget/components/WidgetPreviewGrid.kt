package com.android.maxclub.bluetoothme.feature.controllers.presentation.add_edit_widget.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.android.maxclub.bluetoothme.feature.controllers.domain.models.Widget
import com.android.maxclub.bluetoothme.feature.controllers.domain.models.WidgetSize
import com.android.maxclub.bluetoothme.feature.controllers.presentation.util.components.widgets.ButtonWidget
import com.android.maxclub.bluetoothme.feature.controllers.presentation.util.components.widgets.EmptyWidget
import com.android.maxclub.bluetoothme.feature.controllers.presentation.util.components.widgets.SwitchWidget

@Composable
fun WidgetPreviewGrid(
    columnsCount: Int,
    widget: Widget<*>,
    onChangeWidgetSize: (Widget<*>, WidgetSize) -> Unit,
    onChangeWidgetEnable: (Widget<*>, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val maxWidgetSpan = maxOf(widget.size.span, columnsCount)
    val widgetHorizontalPadding = (12 + 6 * (maxWidgetSpan - widget.size.span)).dp
    val widgetFraction = widget.size.span / maxWidgetSpan.toFloat()

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = widgetHorizontalPadding)
    ) {
        val widgetModifier = Modifier.fillMaxWidth(widgetFraction)

        when (widget) {
            is Widget.Empty -> EmptyWidget(
                widget = widget,
                modifier = widgetModifier
            ) {
                WidgetPreviewOverlay(
                    widget = widget,
                    columnsCount = columnsCount,
                    onChangeSize = onChangeWidgetSize,
                    onEnabledChange = null,
                )
            }

            is Widget.Button -> ButtonWidget(
                widget = widget,
                onAction = { _, _ -> },
                modifier = widgetModifier
            ) {
                WidgetPreviewOverlay(
                    widget = widget,
                    columnsCount = columnsCount,
                    onChangeSize = onChangeWidgetSize,
                    onEnabledChange = onChangeWidgetEnable,
                )
            }

            is Widget.Switch -> SwitchWidget(
                widget = widget,
                onAction = { _, _ -> },
                modifier = widgetModifier
            ) {
                WidgetPreviewOverlay(
                    widget = widget,
                    columnsCount = columnsCount,
                    onChangeSize = onChangeWidgetSize,
                    onEnabledChange = onChangeWidgetEnable,
                )
            }
        }
    }
}