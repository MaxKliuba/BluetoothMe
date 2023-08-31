package com.android.maxclub.bluetoothme.feature.controllers.presentation.add_edit_controller.components.widgets

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconToggleButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.android.maxclub.bluetoothme.R
import com.android.maxclub.bluetoothme.feature.controllers.domain.models.Widget
import com.android.maxclub.bluetoothme.feature.controllers.domain.models.WidgetSize
import java.util.UUID

@Suppress("OPT_IN_IS_NOT_ENABLED")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BasicWidget(
    widget: Widget,
    onChangeSize: (UUID, WidgetSize) -> Unit,
    onChangeReadOnly: (UUID, Boolean) -> Unit,
    onDelete: (UUID) -> Unit,
    onEdit: () -> Unit,
    modifier: Modifier = Modifier,
    isReadOnlyButtonVisible: Boolean = true,
    content: @Composable BoxScope.() -> Unit,
) {
    Card(
        onClick = { println("onClick()") },
        modifier = modifier.height(120.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            IconButton(
                onClick = {
                    onChangeSize(
                        widget.id,
                        WidgetSize.values()[(widget.size.ordinal + 1) % WidgetSize.values().size]
                    )
                },
                modifier = Modifier
                    .size(36.dp)
                    .align(Alignment.TopStart)
            ) {
                Icon(
                    painter = painterResource(
                        id = when (widget.size) {
                            WidgetSize.SMALL -> R.drawable.ic_zoom_out_24
                            WidgetSize.LARGE -> R.drawable.ic_zoom_in_24
                        }
                    ),
                    contentDescription = stringResource(R.string.change_size_button)
                )
            }

            Text(
                text = widget.title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .padding(horizontal = 36.dp, vertical = 6.dp)
                    .align(Alignment.TopCenter)
            )

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

            if (isReadOnlyButtonVisible) {
                FilledIconToggleButton(
                    checked = widget.readOnly,
                    onCheckedChange = { onChangeReadOnly(widget.id, it)},
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .size(36.dp)
                        .padding(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Lock,
                        contentDescription = stringResource(R.string.read_only_button),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            IconButton(
                onClick = onEdit,
                modifier = Modifier
                    .size(36.dp)
                    .align(Alignment.BottomEnd)
            ) {
                Icon(
                    imageVector = Icons.Filled.Edit,
                    contentDescription = stringResource(R.string.edit_widget_button)
                )
            }

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 24.dp)
            ) {
                content()
            }
        }
    }
}