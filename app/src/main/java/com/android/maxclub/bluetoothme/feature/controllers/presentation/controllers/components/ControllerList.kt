package com.android.maxclub.bluetoothme.feature.controllers.presentation.controllers.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import com.android.maxclub.bluetoothme.feature.controllers.domain.models.ControllerWithWidgetCount
import org.burnoutcrew.reorderable.ReorderableItem
import org.burnoutcrew.reorderable.detectReorderAfterLongPress
import org.burnoutcrew.reorderable.rememberReorderableLazyListState
import org.burnoutcrew.reorderable.reorderable
import java.util.UUID

@Composable
fun ControllerList(
    controllers: List<ControllerWithWidgetCount>,
    selectedControllerId: UUID?,
    onOpenController: (UUID) -> Unit,
    onShareController: (UUID) -> Unit,
    onSelectController: (UUID) -> Unit,
    onUnselectController: () -> Unit,
    onReorderController: (Int, Int) -> Unit,
    onApplyChangedControllerPositions: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    val state = rememberReorderableLazyListState(
        onMove = { from, to ->
            onReorderController(from.index, to.index)
        }
    )

    LazyColumn(
        state = state.listState,
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
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
                    } else {
                        onApplyChangedControllerPositions()
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