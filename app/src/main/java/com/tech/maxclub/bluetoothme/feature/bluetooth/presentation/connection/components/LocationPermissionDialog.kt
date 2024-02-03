package com.tech.maxclub.bluetoothme.feature.bluetooth.presentation.connection.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PermDeviceInformation
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.tech.maxclub.bluetoothme.R

@Composable
fun LocationPermissionDialog(
    onDismiss: () -> Unit,
    onAllow: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(id = R.string.location_permission_dialog_title),
                textAlign = TextAlign.Center
            )
        },
        text = {
            Text(text = stringResource(id = R.string.location_permission_dialog_text))
        },
        icon = {
            Icon(
                imageVector = Icons.Default.PermDeviceInformation,
                contentDescription = null
            )
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = stringResource(id = R.string.allow_later_button),
                    color = Color.Gray,
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onAllow) {
                Text(text = stringResource(R.string.allow_button))
            }
        },
        modifier = modifier,
    )
}