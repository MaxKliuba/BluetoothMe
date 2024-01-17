package com.tech.maxclub.bluetoothme.feature.controllers.presentation.util.components.widgets

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun BasicWidget(
    widgetTitle: String,
    modifier: Modifier = Modifier,
    withTitlePadding: Boolean,
    overlay: @Composable BoxScope.() -> Unit,
    content: @Composable BoxScope.() -> Unit,
) {
    Card(modifier = modifier.height(120.dp)) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = widgetTitle,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .padding(horizontal = if (withTitlePadding) 36.dp else 6.dp, vertical = 6.dp)
                    .align(Alignment.TopCenter)
            )

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 24.dp)
            ) {
                content()
            }

            overlay()
        }
    }
}