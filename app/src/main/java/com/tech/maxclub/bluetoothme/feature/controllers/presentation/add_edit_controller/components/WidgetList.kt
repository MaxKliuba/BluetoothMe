package com.tech.maxclub.bluetoothme.feature.controllers.presentation.add_edit_controller.components

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
import com.tech.maxclub.bluetoothme.feature.controllers.domain.models.Widget
import com.tech.maxclub.bluetoothme.feature.controllers.domain.models.WidgetSize
import com.tech.maxclub.bluetoothme.feature.controllers.presentation.util.components.widgets.ButtonWidget
import com.tech.maxclub.bluetoothme.feature.controllers.presentation.util.components.widgets.EmptyWidget
import com.tech.maxclub.bluetoothme.feature.controllers.presentation.util.components.widgets.SliderWidget
import com.tech.maxclub.bluetoothme.feature.controllers.presentation.util.components.widgets.SwitchWidget
import com.tech.maxclub.bluetoothme.feature.controllers.presentation.util.components.widgets.TextWidget
import org.burnoutcrew.reorderable.ReorderableItem
import org.burnoutcrew.reorderable.detectReorderAfterLongPress
import org.burnoutcrew.reorderable.rememberReorderableLazyGridState
import org.burnoutcrew.reorderable.reorderable

@Composable
fun WidgetList(
    columnsCount: Int,
    widgets: List<Widget<*>>,
    onAddWidget: () -> Unit,
    onChangeWidgetSize: (Widget<*>, WidgetSize) -> Unit,
    onChangeWidgetEnable: (Widget<*>, Boolean) -> Unit,
    onReorderLocalWidgets: (fromIndex: Int, toIndex: Int) -> Unit,
    onApplyWidgetsReorder: () -> Unit,
    onEditWidget: (Int, Int) -> Unit,
    onDeleteWidget: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    val state = rememberReorderableLazyGridState(
        onMove = { from, to ->
            onReorderLocalWidgets(from.index, to.index)
        },
        onDragEnd = { _, _ ->
            onApplyWidgetsReorder()
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
                    }
                }

                when (widget) {
                    is Widget.Empty -> EmptyWidget(widget = widget) {
                        AddEditControllerWidgetOverlay(
                            widget = widget,
                            columnsCount = columnsCount,
                            onChangeSize = onChangeWidgetSize,
                            onEnabledChange = null,
                            onEdit = onEditWidget,
                            onDelete = onDeleteWidget,
                        )
                    }

                    is Widget.Button -> ButtonWidget(
                        widget = widget,
                        withTitlePadding = true,
                        onAction = { _, _ -> }
                    ) {
                        AddEditControllerWidgetOverlay(
                            widget = widget,
                            columnsCount = columnsCount,
                            onChangeSize = onChangeWidgetSize,
                            onEnabledChange = onChangeWidgetEnable,
                            onEdit = onEditWidget,
                            onDelete = onDeleteWidget,
                        )
                    }

                    is Widget.Switch -> SwitchWidget(
                        widget = widget,
                        withTitlePadding = true,
                        onAction = { _, _ -> }
                    ) {
                        AddEditControllerWidgetOverlay(
                            widget = widget,
                            columnsCount = columnsCount,
                            onChangeSize = onChangeWidgetSize,
                            onEnabledChange = onChangeWidgetEnable,
                            onEdit = onEditWidget,
                            onDelete = onDeleteWidget,
                        )
                    }

                    is Widget.Slider -> SliderWidget(
                        widget = widget,
                        withTitlePadding = true,
                        isIncDecButtonsVisible = false,
                        onAction = { _, _ -> }
                    ) {
                        AddEditControllerWidgetOverlay(
                            widget = widget,
                            columnsCount = columnsCount,
                            onChangeSize = onChangeWidgetSize,
                            onEnabledChange = onChangeWidgetEnable,
                            onEdit = onEditWidget,
                            onDelete = onDeleteWidget,
                        )
                    }

                    is Widget.Text -> TextWidget(
                        widget = widget,
                        enabled = false,
                        withTitlePadding = true,
                        onAction = { _, _ -> }
                    ) {
                        AddEditControllerWidgetOverlay(
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
        }

        if (!isDragging) {
            item {
                AddNewWidgetItem(onAdd = onAddWidget)
            }
        }
    }
}