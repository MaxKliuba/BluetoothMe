package com.android.maxclub.bluetoothme.feature.controllers.presentation.add_edit_controller.components.widgets

import androidx.compose.foundation.layout.size
import androidx.compose.material3.FilledIconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.android.maxclub.bluetoothme.feature.controllers.domain.models.Widget
import com.android.maxclub.bluetoothme.feature.controllers.domain.models.WidgetSize
import com.android.maxclub.bluetoothme.feature.controllers.presentation.util.AsIcon

@Composable
fun ButtonWidget(
    widget: Widget.Button,
    columnsCount: Int,
    onChangeSize: (Widget<*>, WidgetSize) -> Unit,
    onEnabledChange: (Widget<*>, Boolean) -> Unit,
    onEdit: (Int) -> Unit,
    onDelete: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    BasicWidget(
        widget = widget,
        columnsCount = columnsCount,
        onChangeSize = onChangeSize,
        onEnabledChange = onEnabledChange,
        onDelete = onDelete,
        onEdit = onEdit,
        modifier = modifier,
    ) {
        FilledIconButton(
            onClick = { },
            modifier = Modifier.size(64.dp)
        ) {
            widget.icon.AsIcon()
        }
    }
}