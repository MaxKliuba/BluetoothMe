package com.android.maxclub.bluetoothme.feature.controllers.presentation.add_edit_controller.components.widgets

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.android.maxclub.bluetoothme.feature.controllers.domain.models.Widget
import com.android.maxclub.bluetoothme.feature.controllers.domain.models.WidgetSize
import java.util.UUID

@Composable
fun ButtonWidget(
    widget: Widget.Button,
    columnsCount:  Int,
    onChangeSize: (UUID, WidgetSize) -> Unit,
    onEnabledChange: (UUID, Boolean) -> Unit,
    onDelete: (UUID) -> Unit,
    onEdit: (UUID) -> Unit,
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
            onClick = {},
            modifier = Modifier.size(64.dp)
        ) {
            Icon(imageVector = Icons.Default.PowerSettingsNew, contentDescription = null)
        }
    }
}