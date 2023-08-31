package com.android.maxclub.bluetoothme.feature.controllers.presentation.add_edit_controller.components.widgets

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.android.maxclub.bluetoothme.feature.controllers.domain.models.Widget
import com.android.maxclub.bluetoothme.feature.controllers.domain.models.WidgetSize
import java.util.UUID

@Composable
fun EmptyWidget(
    widget: Widget.Empty,
    onChangeSize: (UUID, WidgetSize) -> Unit,
    onDelete: (UUID) -> Unit,
    onEdit: () -> Unit,
    modifier: Modifier = Modifier
) {
    BasicWidget(
        widget = widget,
        isReadOnlyButtonVisible = false,
        onChangeSize = onChangeSize,
        onChangeReadOnly = { _, _ -> },
        onDelete = onDelete,
        onEdit = onEdit,
        modifier = modifier,
    ) {
        // Empty
    }
}