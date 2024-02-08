package com.tech.maxclub.bluetoothme.feature.controllers.presentation.controllers.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import com.tech.maxclub.bluetoothme.feature.controllers.domain.models.ControllerWithWidgetCount
import org.burnoutcrew.reorderable.ReorderableItem
import org.burnoutcrew.reorderable.detectReorderAfterLongPress
import org.burnoutcrew.reorderable.rememberReorderableLazyListState
import org.burnoutcrew.reorderable.reorderable

@Composable
fun ControllerList(
    controllers: List<ControllerWithWidgetCount>,
    selectedControllerId: Int?,
    onOpenController: (Int) -> Unit,
    onShareController: (Int) -> Unit,
    onSelectController: (Int) -> Unit,
    onUnselectController: () -> Unit,
    onReorderLocalControllers: (fromIndex: Int, toIndex: Int) -> Unit,
    onApplyControllersReorder: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    val state = rememberReorderableLazyListState(
        onMove = { from, to ->
            onReorderLocalControllers(from.index, to.index)
        },
        onDragEnd = { _, _ ->
            onApplyControllersReorder()
        }
    )

    LazyColumn(
        state = state.listState,
        contentPadding = PaddingValues(start = 8.dp, top = 8.dp, end = 8.dp, bottom = 80.dp),
        modifier = modifier
            .reorderable(state)
            .detectReorderAfterLongPress(state)
    ) {
        items(
            items = controllers,
            key = { it.controller.id },
        ) { controller ->
            val isSelected = controller.controller.id == selectedControllerId
            val isDragging = selectedControllerId != null

            ReorderableItem(
                state = state,
                key = controller.controller.id
            ) { isDraggingFlag ->
                LaunchedEffect(isDraggingFlag) {
                    if (isDraggingFlag) {
                        onSelectController(controller.controller.id)
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    }
                }

                if (isDragging) {
                    ControllerDraggingItem(
                        controller = controller,
                        isSelected = isSelected,
                        onClick = {
                            if (isSelected) onUnselectController()
                            else onSelectController(controller.controller.id)
                        },
                    )
                } else {
                    ControllerItem(
                        controller = controller,
                        onClick = onOpenController,
                        onShare = onShareController,
                    )
                }
            }
        }
    }
}