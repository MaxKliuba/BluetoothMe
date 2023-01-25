package com.android.maxclub.bluetoothme.presentation.connection.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.core.graphics.ColorUtils
import com.android.maxclub.bluetoothme.domain.bluetooth.model.BluetoothDevice

@Composable
fun BluetoothDeviceConnectingItem(
    device: BluetoothDevice,
    onClickIcon: (String) -> Unit,
    onClickItem: (BluetoothDevice) -> Unit,
    modifier: Modifier = Modifier,
) {
    val backgroundColor = Color(
        ColorUtils.blendARGB(
            MaterialTheme.colorScheme.outline.toArgb(),
            MaterialTheme.colorScheme.background.toArgb(),
            0.7f,
        )
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .background(color = backgroundColor)
            .clickable { onClickItem(device) }
    ) {
        Spacer(modifier = Modifier.width(20.dp))

        BluetoothDeviceIcon(
            deviceType = device.type,
            onClick = onClickIcon,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        BluetoothDeviceInfoSection(
            isFavorite = device.isFavorite,
            deviceName = device.name,
            deviceAddress = device.address,
            connectionType = null,
            modifier = Modifier
                .padding(vertical = 16.dp)
                .weight(weight = 1.0f)
        )

        Spacer(modifier = Modifier.width(16.dp))

        ConnectionTypeChips(
            device = device,
            onSelect = null,
        )

        Spacer(modifier = Modifier.width(12.dp))
    }
}