package com.android.maxclub.bluetoothme.feature.controllers.presentation.add_edit_controller.components

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.FilledIconToggleButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.android.maxclub.bluetoothme.R
import com.android.maxclub.bluetoothme.feature.controllers.domain.models.Widget
import com.android.maxclub.bluetoothme.feature.controllers.domain.models.WidgetSize

@Composable
fun BoxScope.AddEditControllerWidgetOverlay(
    widget: Widget<*>,
    columnsCount: Int,
    onChangeSize: (Widget<*>, WidgetSize) -> Unit,
    onEnabledChange: ((Widget<*>, Boolean) -> Unit)?,
    onEdit: (Int) -> Unit,
    onDelete: (Int) -> Unit,
) {
    IconButton(
        onClick = { onChangeSize(widget, widget.size.next(limit = columnsCount)) },
        modifier = Modifier
            .size(36.dp)
            .align(Alignment.TopStart)
    ) {
        Icon(
            painter = painterResource(
                id = if (widget.size.span != columnsCount) {
                    R.drawable.ic_zoom_out_24
                } else {
                    R.drawable.ic_zoom_in_24
                }
            ),
            contentDescription = stringResource(R.string.change_size_button)
        )
    }

    IconButton(
        onClick = { onDelete(widget.id) },
        modifier = Modifier
            .size(36.dp)
            .align(Alignment.TopEnd)
    ) {
        Icon(
            imageVector = Icons.Filled.Close,
            contentDescription = stringResource(R.string.delete_widget_button)
        )
    }

    onEnabledChange?.let {
        FilledIconToggleButton(
            checked = !widget.enabled,
            onCheckedChange = { onEnabledChange(widget, !it) },
            modifier = Modifier
                .align(Alignment.BottomStart)
                .size(34.dp)
                .padding(4.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Lock,
                contentDescription = stringResource(R.string.enabled_button),
                modifier = Modifier.size(20.dp)
            )
        }
    }

    IconButton(
        onClick = { onEdit(widget.id) },
        modifier = Modifier
            .size(36.dp)
            .align(Alignment.BottomEnd)
    ) {
        Icon(
            imageVector = Icons.Filled.Edit,
            contentDescription = stringResource(R.string.edit_widget_button)
        )
    }
}