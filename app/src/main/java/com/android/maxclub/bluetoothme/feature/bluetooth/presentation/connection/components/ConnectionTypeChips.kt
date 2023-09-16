package com.android.maxclub.bluetoothme.feature.bluetooth.presentation.connection.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.android.maxclub.bluetoothme.R
import com.android.maxclub.bluetoothme.feature.bluetooth.domain.bluetooth.models.BluetoothDevice
import com.android.maxclub.bluetoothme.feature.bluetooth.domain.bluetooth.models.BluetoothLeProfile
import com.android.maxclub.bluetoothme.feature.bluetooth.domain.bluetooth.models.ConnectionType
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConnectionTypeChips(
    device: BluetoothDevice,
    onSelect: ((BluetoothDevice, ConnectionType) -> Unit)?,
    onReselect: ((BluetoothDevice, BluetoothLeProfile) -> Unit)?,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier) {
        device.type.availableConnectionTypes.forEach { connectionType ->
            val isSelected = connectionType::class == device.type.connectionType::class

            FilterChip(
                enabled = onSelect != null,
                selected = isSelected,
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
                onClick = {
                    if (isSelected) {
                        if (connectionType is ConnectionType.Ble) {
                            onReselect?.invoke(device, connectionType.profile)
                        }
                    } else {
                        onSelect?.invoke(device, connectionType)
                    }
                },
            )

            Spacer(modifier = Modifier.width(8.dp))
        }
    }
}