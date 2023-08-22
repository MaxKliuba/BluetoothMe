package com.android.maxclub.bluetoothme.feature.controllers.presentation.controllers.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.android.maxclub.bluetoothme.R
import com.android.maxclub.bluetoothme.feature.controllers.domain.models.ControllerWithWidgetCount

@Suppress("OPT_IN_IS_NOT_ENABLED")
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ControllerWithWidgetCountItem(
    controllerWithWidgetCount: ControllerWithWidgetCount,
    onClick: (Int) -> Unit,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundShape = RoundedCornerShape(16.dp)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .border(
                width = 1.dp,
                shape = backgroundShape,
                color = MaterialTheme.colorScheme.secondary,
            )
            .clip(backgroundShape)
            .combinedClickable(
                onClick = { onClick(controllerWithWidgetCount.controller.id) },
                onLongClick = { onSelect(controllerWithWidgetCount.controller.id) },
            )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = controllerWithWidgetCount.controller.title,
                style = MaterialTheme.typography.titleLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = pluralStringResource(
                    id = R.plurals.widget_plural,
                    count = controllerWithWidgetCount.widgetCount,
                    controllerWithWidgetCount.widgetCount,
                ),
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}