package com.android.maxclub.bluetoothme.presentation.connection.components

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
import com.android.maxclub.bluetoothme.domain.bluetooth.model.BluetoothDevice
import com.android.maxclub.bluetoothme.domain.bluetooth.model.ConnectionType

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
                                ConnectionType.Classic -> R.string.bluetooth_classic
                                ConnectionType.Ble -> R.string.bluetooth_le
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