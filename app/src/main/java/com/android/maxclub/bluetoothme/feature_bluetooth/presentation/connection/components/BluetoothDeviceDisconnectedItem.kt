package com.android.maxclub.bluetoothme.feature_bluetooth.presentation.connection.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.android.maxclub.bluetoothme.feature_bluetooth.domain.bluetooth.models.BluetoothDevice
import com.android.maxclub.bluetoothme.feature_bluetooth.domain.bluetooth.models.ConnectionType

@Composable
fun BluetoothDeviceItem(
    device: BluetoothDevice,
    onClickIcon: (String) -> Unit,
    onClickItem: (BluetoothDevice) -> Unit,
    onSelectConnectionType: (BluetoothDevice, ConnectionType) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
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
            isBonded = if (device.isBonded) null else false,
            connectionType = null,
            modifier = Modifier
                .padding(vertical = 16.dp)
                .weight(weight = 1f)
        )

        Spacer(modifier = Modifier.width(16.dp))

        ConnectionTypeChips(
            device = device,
            onSelect = onSelectConnectionType,
            onReselect = onSelectConnectionType,
        )

        Spacer(modifier = Modifier.width(12.dp))
    }
}