package com.android.maxclub.bluetoothme.feature.main.presentation.main.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.android.maxclub.bluetoothme.R

@Composable
fun BluetoothPermissionRationaleDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = stringResource(R.string.bluetooth_permission_dialog_title))
        },
        text = {
            Text(text = stringResource(R.string.bluetooth_permission_dialog_text))
        },
        icon = {
            Icon(
                painter = painterResource(id = R.drawable.ic_permission_24),
                contentDescription = null
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(text = stringResource(R.string.allow_in_settings_button))
            }
        },
        modifier = modifier,
    )
}