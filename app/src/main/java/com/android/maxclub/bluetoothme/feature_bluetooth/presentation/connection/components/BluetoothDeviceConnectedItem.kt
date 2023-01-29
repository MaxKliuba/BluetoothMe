package com.android.maxclub.bluetoothme.feature_bluetooth.presentation.connection.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.android.maxclub.bluetoothme.R
import com.android.maxclub.bluetoothme.feature_bluetooth.domain.bluetooth.models.BluetoothDevice
import com.android.maxclub.bluetoothme.feature_bluetooth.domain.bluetooth.models.ConnectionType

@Composable
fun BluetoothDeviceConnectedItem(
    device: BluetoothDevice,
    onClickIcon: (String) -> Unit,
    onClickItem: (BluetoothDevice) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .padding(20.dp)
            .background(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(24.dp),
            )
    ) {
        Spacer(modifier = Modifier.width(20.dp))

        BluetoothDeviceIcon(
            deviceType = device.type,
            onClick = onClickIcon,
            modifier = Modifier.padding(vertical = 20.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        BluetoothDeviceInfoSection(
            isFavorite = false,
            deviceName = device.name,
            deviceAddress = device.address,
            isBonded = null,
            connectionType = device.type.connectionType,
            modifier = Modifier
                .padding(vertical = 16.dp)
                .weight(weight = 1.0f)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Switch(
            checked = true,
            onCheckedChange = { onClickItem(device) },
        )

        Spacer(modifier = Modifier.width(20.dp))
    }

    Divider()
}