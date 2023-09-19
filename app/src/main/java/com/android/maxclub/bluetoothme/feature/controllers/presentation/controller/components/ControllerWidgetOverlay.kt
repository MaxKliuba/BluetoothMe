package com.android.maxclub.bluetoothme.feature.controllers.presentation.controller.components

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.android.maxclub.bluetoothme.R

@Composable
fun BoxScope.ControllerWidgetOverlay(isWidgetEnabled: Boolean) {
    if (!isWidgetEnabled) {
        Icon(
            imageVector = Icons.Filled.Lock,
            contentDescription = stringResource(R.string.enabled_button),
            modifier = Modifier
                .align(Alignment.TopStart)
                .size(32.dp)
                .padding(6.dp)
        )
    }
}