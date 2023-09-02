package com.android.maxclub.bluetoothme.feature.controllers.presentation.controllers.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.android.maxclub.bluetoothme.feature.controllers.domain.models.ControllerWithWidgetCount
import java.util.UUID

@Suppress("OPT_IN_IS_NOT_ENABLED")
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ControllerList(
    controllers: List<ControllerWithWidgetCount>,
    onEditController: (UUID) -> Unit,
    onDeleteController: (UUID) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        modifier = modifier
    ) {
        items(
            items = controllers,
            key = { it.controller.id }
        ) { controller ->
            ControllerItem(
                controller = controller,
                onClick = onEditController,
                onSelect = onDeleteController,
//                modifier = Modifier.animateItemPlacement(),
            )
        }
    }
}