package com.tech.maxclub.bluetoothme.feature.controllers.presentation.controllers.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DragIndicator
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.tech.maxclub.bluetoothme.R
import com.tech.maxclub.bluetoothme.feature.controllers.domain.models.ControllerWithWidgetCount

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ControllerDraggingItem(
    controller: ControllerWithWidgetCount,
    isSelected: Boolean,
    onClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val cardColors = if (isSelected) {
        CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
    } else {
        CardDefaults.cardColors()
    }

    Card(
        onClick = { onClick(controller.controller.id) },
        colors = cardColors,
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = controller.controller.title,
                    style = MaterialTheme.typography.titleLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = pluralStringResource(
                        id = R.plurals.widget_plural,
                        count = controller.widgetCount,
                        controller.widgetCount,
                    ),
                    style = MaterialTheme.typography.bodySmall,
                )
            }

            Icon(imageVector = Icons.Default.DragIndicator, contentDescription = null)
        }
    }
}