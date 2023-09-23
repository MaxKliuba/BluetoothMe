package com.android.maxclub.bluetoothme.feature.main.presentation.main.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.android.maxclub.bluetoothme.R
import com.android.maxclub.bluetoothme.feature.main.presentation.main.util.launchPermissionSettingsIntent

@Composable
fun PermissionRationaleDialog(
    title: String,
    text: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = title,
                textAlign = TextAlign.Center
            )
        },
        text = {
            Text(text = text)
        },
        icon = {
            Icon(
                painter = painterResource(id = R.drawable.ic_permission_24),
                contentDescription = null
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onDismiss()
                    launchPermissionSettingsIntent(context)
                }
            ) {
                Text(text = stringResource(R.string.allow_in_settings_button))
            }
        },
        modifier = modifier,
    )
}