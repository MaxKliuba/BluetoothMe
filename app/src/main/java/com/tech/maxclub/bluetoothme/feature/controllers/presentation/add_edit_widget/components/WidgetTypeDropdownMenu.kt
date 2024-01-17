package com.tech.maxclub.bluetoothme.feature.controllers.presentation.add_edit_widget.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.tech.maxclub.bluetoothme.feature.controllers.domain.models.WidgetType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WidgetTypeDropdownMenu(
    selectedWidgetType: WidgetType,
    widgetTypes: Array<WidgetType>,
    onSelectWidgetType: (WidgetType) -> Unit,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }
    val onExpandedChange: (Boolean) -> Unit = { isExpanded = it }

    Box(modifier = modifier) {
        ExposedDropdownMenuBox(
            expanded = isExpanded,
            onExpandedChange = onExpandedChange,
        ) {
            TextField(
                value = selectedWidgetType.name,
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )

            ExposedDropdownMenu(
                expanded = isExpanded,
                onDismissRequest = { onExpandedChange(false) },
            ) {
                widgetTypes.forEach { widgetType ->
                    DropdownMenuItem(
                        text = {
                            Text(text = widgetType.name)
                        },
                        onClick = {
                            onSelectWidgetType(widgetType)
                            onExpandedChange(false)
                        }
                    )
                }
            }
        }
    }
}