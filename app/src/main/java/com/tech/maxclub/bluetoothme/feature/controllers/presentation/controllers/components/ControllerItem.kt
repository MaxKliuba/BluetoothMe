package com.tech.maxclub.bluetoothme.feature.controllers.presentation.controllers.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.tech.maxclub.bluetoothme.R
import com.tech.maxclub.bluetoothme.feature.controllers.domain.models.ControllerWithWidgetCount

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ControllerItem(
    controller: ControllerWithWidgetCount,
    onClick: (Int) -> Unit,
    onShare: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = { onClick(controller.controller.id) },
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 16.dp, top = 16.dp, end = 8.dp, bottom = 16.dp)
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

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(onClick = { onShare(controller.controller.id) }) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = stringResource(R.string.share_controller_button)
                )
            }
        }
    }
}