package com.android.maxclub.bluetoothme.feature_bluetooth.presentation.connection.components

import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.android.maxclub.bluetoothme.feature_bluetooth.presentation.connection.util.DeviceIcon

@Composable
fun BluetoothDeviceIcon(
    deviceIcon: DeviceIcon,
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    FilledTonalIconButton(
        onClick = { onClick(deviceIcon.description) },
        colors = IconButtonDefaults.filledTonalIconButtonColors(
            containerColor = deviceIcon.backgroundColor,
            contentColor = deviceIcon.tintColor,
        ),
        modifier = modifier,
    ) {
        Icon(
            painter = painterResource(id = deviceIcon.iconResId),
            contentDescription = deviceIcon.description,
        )
    }
}