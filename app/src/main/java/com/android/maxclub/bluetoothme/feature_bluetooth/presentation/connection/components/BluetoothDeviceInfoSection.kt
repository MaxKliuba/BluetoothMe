package com.android.maxclub.bluetoothme.feature_bluetooth.presentation.connection.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.android.maxclub.bluetoothme.R

@Composable
fun BluetoothDeviceInfoSection(
    isFavorite: Boolean,
    deviceName: String,
    deviceAddress: String,
    connectionType: String?,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = deviceName,
                style = MaterialTheme.typography.titleLarge,
            )
            if (isFavorite) {
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    painter = painterResource(id = R.drawable.ic_favorite_24),
                    contentDescription = stringResource(id = R.string.favorite_label),
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(20.dp),
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "$deviceAddress${connectionType?.let { " | $it" } ?: ""}",
            style = MaterialTheme.typography.bodySmall
        )
    }
}