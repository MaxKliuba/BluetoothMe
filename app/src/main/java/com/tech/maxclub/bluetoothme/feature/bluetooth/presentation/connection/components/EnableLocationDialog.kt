package com.tech.maxclub.bluetoothme.feature.bluetooth.presentation.connection.components

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.tech.maxclub.bluetoothme.R

@Composable
fun EnableLocationDialog(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(R.string.enable_location_dialog_title),
                textAlign = TextAlign.Center
            )
        },
        text = {
            Text(text = stringResource(id = R.string.location_permission_dialog_text))
        },
        icon = {
            Icon(
                imageVector = Icons.Default.MyLocation,
                contentDescription = null
            )
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = stringResource(R.string.close_dialog_buton),
                    color = Color.Gray,
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onDismiss()
                    launchLocationSettingIntent(context)
                }
            ) {
                Text(text = stringResource(R.string.enable_in_settings_button))
            }
        },
        modifier = modifier,
    )
}

private fun launchLocationSettingIntent(context: Context) {
    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
    context.startActivity(intent)
}