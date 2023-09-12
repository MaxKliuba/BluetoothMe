package com.android.maxclub.bluetoothme.feature.controllers.presentation.controller.components

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
import com.android.maxclub.bluetoothme.feature.controllers.presentation.controller.components.widgets.ButtonWidget
import com.android.maxclub.bluetoothme.feature.controllers.presentation.controller.components.widgets.EmptyWidget
import com.android.maxclub.bluetoothme.feature.controllers.presentation.controller.components.widgets.SwitchWidget

@Composable
fun WidgetList(
    columnsCount: Int,
    widgets: List<Widget<*>>,
    onAction: (String, String) -> Unit,
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
                is Widget.Empty -> EmptyWidget(widget = widget)

                is Widget.Button -> ButtonWidget(
                    widget = widget,
                    onAction = onAction,
                )

                is Widget.Switch -> SwitchWidget(
                    widget = widget,
                    onAction = onAction,
                )
            }
        }
    }
}