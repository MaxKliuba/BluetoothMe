package com.tech.maxclub.bluetoothme.feature.bluetooth.presentation.connection.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.tech.maxclub.bluetoothme.feature.bluetooth.domain.bluetooth.models.BluetoothDevice
import com.tech.maxclub.bluetoothme.feature.bluetooth.domain.bluetooth.models.BluetoothLeProfile
import com.tech.maxclub.bluetoothme.feature.bluetooth.domain.bluetooth.models.ConnectionType
import com.tech.maxclub.bluetoothme.feature.bluetooth.presentation.connection.util.toDeviceIcon

@Composable
fun BluetoothDeviceDisconnectedItem(
    device: BluetoothDevice,
    onClickIcon: (String) -> Unit,
    onClickItem: (BluetoothDevice) -> Unit,
    onSetConnectionType: (BluetoothDevice, ConnectionType) -> Unit,
    onShowBleProfileDialog: (BluetoothDevice, BluetoothLeProfile) -> Unit,
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
            deviceIcon = device.type.toDeviceIcon(LocalContext.current),
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
            onSelect = onSetConnectionType,
            onReselect = onShowBleProfileDialog,
        )

        Spacer(modifier = Modifier.width(12.dp))
    }
}