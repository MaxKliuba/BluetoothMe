package com.android.maxclub.bluetoothme.presentation.connection.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.graphics.ColorUtils
import com.android.maxclub.bluetoothme.R
import com.android.maxclub.bluetoothme.domain.bluetooth.model.DeviceType
import com.android.maxclub.bluetoothme.ui.theme.BabyBlue
import com.android.maxclub.bluetoothme.ui.theme.LightGreen
import com.android.maxclub.bluetoothme.ui.theme.RedOrange
import com.android.maxclub.bluetoothme.ui.theme.Violet

@Composable
fun BluetoothDeviceIcon(
    deviceType: DeviceType,
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val (color, icon) = when (deviceType) {
        is DeviceType.Classic -> BabyBlue to (R.drawable.ic_bluetooth_classic_24 to R.string.bluetooth_classic_device)
        is DeviceType.Ble -> Violet to (R.drawable.ic_bluetooth_le_24 to R.string.bluetooth_le_device)
        is DeviceType.Dual -> RedOrange to (R.drawable.ic_bluetooth_dual_24 to R.string.bluetooth_dual_device)
        is DeviceType.Unknown -> LightGreen to (R.drawable.ic_bluetooth_unknown_24 to R.string.bluetooth_unknown_device)
    }
    val painter = painterResource(id = icon.first)
    val contentDescription = stringResource(id = icon.second)
    val shape = CircleShape

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(36.dp)
            .background(
                color = color,
                shape = shape,
            )
            .clip(shape)
            .clickable { onClick(contentDescription) }
    ) {
        Icon(
            painter = painter,
            contentDescription = contentDescription,
            tint = Color(ColorUtils.blendARGB(color.toArgb(), Color.Black.toArgb(), 0.5f)),
        )
    }
}