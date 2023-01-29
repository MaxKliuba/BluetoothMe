package com.android.maxclub.bluetoothme.feature_bluetooth.presentation.connection.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.android.maxclub.bluetoothme.R
import com.android.maxclub.bluetoothme.feature_bluetooth.domain.bluetooth.models.BluetoothDevice
import com.android.maxclub.bluetoothme.feature_bluetooth.domain.bluetooth.models.ConnectionType

@Suppress("OPT_IN_IS_NOT_ENABLED")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConnectionTypeChips(
    device: BluetoothDevice,
    onSelect: ((BluetoothDevice, ConnectionType) -> Unit)?,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier = modifier
    ) {
        items(
            items = device.type.availableConnectionTypes,
        ) { connectionType ->
            FilterChip(
                enabled = onSelect != null,
                selected = connectionType == device.type.connectionType,
                label = {
                    Text(
                        text = stringResource(
                            id = when (connectionType) {
                                is ConnectionType.Classic -> R.string.bluetooth_classic
                                is ConnectionType.Ble -> R.string.bluetooth_le
                            }
                        )
                    )
                },
                onClick = { onSelect?.invoke(device, connectionType) },
            )

            Spacer(modifier = Modifier.width(8.dp))
        }
    }
}