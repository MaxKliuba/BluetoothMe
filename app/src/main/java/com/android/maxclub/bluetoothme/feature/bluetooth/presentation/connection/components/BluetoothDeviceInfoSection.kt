package com.android.maxclub.bluetoothme.feature.bluetooth.presentation.connection.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.android.maxclub.bluetoothme.R
import com.android.maxclub.bluetoothme.feature.bluetooth.domain.bluetooth.models.ConnectionType

@Composable
fun BluetoothDeviceInfoSection(
    isFavorite: Boolean,
    deviceName: String,
    deviceAddress: String,
    connectionType: ConnectionType?,
    isBonded: Boolean?,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = deviceName,
                style = MaterialTheme.typography.titleLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
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

        val connectionTypeStr = when (connectionType) {
            is ConnectionType.Classic -> stringResource(id = R.string.bluetooth_classic)
            is ConnectionType.Ble -> stringResource(id = R.string.bluetooth_le)
            null -> null
        }
        val bondState = when (isBonded) {
            true -> stringResource(id = R.string.state_bonded_device)
            false -> stringResource(id = R.string.state_not_bonded_device)
            null -> null
        }

        Text(
            text = "$deviceAddress${
                bondState?.let { " | $it" } ?: ""
            }${
                connectionTypeStr?.let { " | $it" } ?: ""
            }",
            style = MaterialTheme.typography.bodySmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}