package com.android.maxclub.bluetoothme.feature.controllers.presentation.controller.components.widgets

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.android.maxclub.bluetoothme.R
import com.android.maxclub.bluetoothme.feature.controllers.domain.models.Widget

@Composable
fun BasicWidget(
    widget: Widget<*>,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    Card(modifier = modifier.height(120.dp)) {
        Box(modifier = Modifier.fillMaxSize()) {
            Text(
                text = widget.title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .padding(6.dp)
                    .align(Alignment.TopCenter)
            )

            if (!widget.enabled) {
                Icon(
                    imageVector = Icons.Filled.Lock,
                    contentDescription = stringResource(R.string.enabled_button),
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .size(32.dp)
                        .padding(6.dp)
                )
            }

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 24.dp)
            ) {
                content()
            }
        }
    }
}