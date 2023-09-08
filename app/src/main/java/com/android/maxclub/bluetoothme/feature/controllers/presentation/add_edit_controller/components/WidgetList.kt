package com.android.maxclub.bluetoothme.feature.controllers.presentation.add_edit_controller.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import com.android.maxclub.bluetoothme.feature.controllers.domain.models.Widget
import com.android.maxclub.bluetoothme.feature.controllers.domain.models.WidgetSize
import com.android.maxclub.bluetoothme.feature.controllers.presentation.add_edit_controller.components.widgets.ButtonWidget
import com.android.maxclub.bluetoothme.feature.controllers.presentation.add_edit_controller.components.widgets.EmptyWidget
import org.burnoutcrew.reorderable.ReorderableItem
import org.burnoutcrew.reorderable.detectReorderAfterLongPress
import org.burnoutcrew.reorderable.rememberReorderableLazyGridState
import org.burnoutcrew.reorderable.reorderable

@Composable
fun WidgetList(
    columnsCount: Int,
    widgets: List<Widget>,
    onAddWidget: () -> Unit,
    onChangeWidgetSize: (Widget, WidgetSize) -> Unit,
    onChangeWidgetEnable: (Widget, Boolean) -> Unit,
    onReorderWidget: (Int, Int) -> Unit,
    onApplyChangedWidgetPositions: () -> Unit,
    onEditWidget: (Int) -> Unit,
    onDeleteWidget: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    val state = rememberReorderableLazyGridState(
        onMove = { from, to ->
            onReorderWidget(from.index, to.index)
        }
    )
    var isDragging by remember { mutableStateOf(false) }

    LazyVerticalGrid(
        state = state.gridState,
        columns = GridCells.Fixed(count = columnsCount),
        contentPadding = PaddingValues(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier
            .reorderable(state)
            .detectReorderAfterLongPress(state)
    ) {
        items(
            items = widgets,
            key = { it.id },
            span = { GridItemSpan(minOf(it.size.span, columnsCount)) },
        ) { widget ->
            ReorderableItem(
                reorderableState = state,
                key = widget.id,
            ) { isDraggingFlag ->
                LaunchedEffect(isDraggingFlag) {
                    isDragging = isDraggingFlag

                    if (isDraggingFlag) {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    } else {
                        onApplyChangedWidgetPositions()
                    }
                }

                when (widget) {
                    is Widget.Empty -> EmptyWidget(
                        widget = widget,
                        columnsCount = columnsCount,
                        onChangeSize = onChangeWidgetSize,
                        onEdit = onEditWidget,
                        onDelete = onDeleteWidget,
                    )

                    is Widget.Button -> ButtonWidget(
                        widget = widget,
                        columnsCount = columnsCount,
                        onChangeSize = onChangeWidgetSize,
                        onEnabledChange = onChangeWidgetEnable,
                        onEdit = onEditWidget,
                        onDelete = onDeleteWidget,
                    )
                }
            }
        }

        if (!isDragging) {
            item {
                AddNewWidgetItem(onAdd = onAddWidget)
            }
        }
    }
}