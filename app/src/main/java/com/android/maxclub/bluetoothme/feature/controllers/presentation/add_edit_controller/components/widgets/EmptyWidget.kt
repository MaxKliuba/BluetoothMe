package com.android.maxclub.bluetoothme.feature.controllers.presentation.add_edit_controller.components.widgets

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.android.maxclub.bluetoothme.feature.controllers.domain.models.Widget
import com.android.maxclub.bluetoothme.feature.controllers.domain.models.WidgetSize

@Composable
fun EmptyWidget(
    widget: Widget.Empty,
    columnsCount: Int,
    onChangeSize: (Widget, WidgetSize) -> Unit,
    onEdit: (Int) -> Unit,
    onDelete: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    BasicWidget(
        widget = widget,
        columnsCount = columnsCount,
        isEnabledButtonVisible = false,
        onChangeSize = onChangeSize,
        onEnabledChange = { _, _ -> },
        onEdit = onEdit,
        onDelete = onDelete,
        modifier = modifier,
    ) {
        // Empty
    }
}