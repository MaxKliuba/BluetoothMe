package com.android.maxclub.bluetoothme.feature.controllers.presentation.add_edit_widget.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FilledIconToggleButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.android.maxclub.bluetoothme.feature.controllers.domain.models.WidgetIcon
import com.android.maxclub.bluetoothme.feature.controllers.presentation.util.components.AsIcon

@Composable
fun WidgetIconList(
    selectedWidgetIcon: WidgetIcon,
    widgetIcons: Array<WidgetIcon>,
    onSelectWidgetIcon: (WidgetIcon) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        contentPadding = PaddingValues(20.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
    ) {
        items(
            items = widgetIcons,
            key = { it.ordinal },
        ) { widgetIcon ->
            FilledIconToggleButton(
                checked = widgetIcon == selectedWidgetIcon,
                onCheckedChange = { checked ->
                    if (checked) {
                        onSelectWidgetIcon(widgetIcon)
                    }
                },
                modifier = Modifier.size(48.dp)
            ) {
                widgetIcon.AsIcon()
            }
        }
    }
}