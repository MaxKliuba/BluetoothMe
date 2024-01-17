package com.tech.maxclub.bluetoothme.feature.controllers.presentation.add_edit_widget.components

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
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
import com.tech.maxclub.bluetoothme.R
import com.tech.maxclub.bluetoothme.feature.controllers.domain.models.Widget
import com.tech.maxclub.bluetoothme.feature.controllers.domain.models.WidgetSize

@Composable
fun BoxScope.WidgetPreviewOverlay(
    widget: Widget<*>,
    columnsCount: Int,
    onChangeSize: (Widget<*>, WidgetSize) -> Unit,
    onEnabledChange: ((Widget<*>, Boolean) -> Unit)?,
) {
    onEnabledChange?.let {
        FilledIconToggleButton(
            checked = !widget.enabled,
            onCheckedChange = { onEnabledChange(widget, !it) },
            modifier = Modifier
                .align(Alignment.TopStart)
                .size(34.dp)
                .padding(2.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Lock,
                contentDescription = stringResource(R.string.enabled_button),
                modifier = Modifier.size(20.dp)
            )
        }
    }

    IconButton(
        onClick = { onChangeSize(widget, widget.size.next(limit = columnsCount)) },
        modifier = Modifier
            .size(32.dp)
            .align(Alignment.BottomStart)
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
}