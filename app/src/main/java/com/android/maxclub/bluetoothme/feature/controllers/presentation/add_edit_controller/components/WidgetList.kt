package com.android.maxclub.bluetoothme.feature.controllers.presentation.add_edit_controller.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.android.maxclub.bluetoothme.feature.controllers.domain.models.Widget
import com.android.maxclub.bluetoothme.feature.controllers.domain.models.WidgetSize
import com.android.maxclub.bluetoothme.feature.controllers.presentation.add_edit_controller.components.widgets.ButtonWidget
import com.android.maxclub.bluetoothme.feature.controllers.presentation.add_edit_controller.components.widgets.EmptyWidget
import java.util.UUID

@Suppress("OPT_IN_IS_NOT_ENABLED")
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WidgetList(
    columnsCount: Int,
    widgets: List<Widget>,
    onAddWidget: () -> Unit,
    onEditWidget: (UUID) -> Unit,
    onChangeWidgetSize: (Widget, WidgetSize) -> Unit,
    onChangeWidgetEnable: (Widget, Boolean) -> Unit,
    onDeleteWidget: (UUID) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(count = columnsCount),
        contentPadding = PaddingValues(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier
    ) {
        items(
            items = widgets,
            key = { it.id },
            span = { GridItemSpan(minOf(it.size.span, columnsCount)) },
        ) { widget ->
            when (widget) {
                is Widget.Empty -> EmptyWidget(
                    widget = widget,
                    columnsCount = columnsCount,
                    onChangeSize = onChangeWidgetSize,
                    onDelete = onDeleteWidget,
                    onEdit = onEditWidget,
//                    modifier = Modifier.animateItemPlacement()
                )

                is Widget.Button -> ButtonWidget(
                    widget = widget,
                    columnsCount = columnsCount,
                    onChangeSize = onChangeWidgetSize,
                    onEnabledChange = onChangeWidgetEnable,
                    onDelete = onDeleteWidget,
                    onEdit = onEditWidget,
//                    modifier = Modifier.animateItemPlacement()
                )
            }
        }

        item {
            AddNewWidgetItem(
                onAdd = onAddWidget,
//                modifier = Modifier.animateItemPlacement()
            )
        }
    }
}