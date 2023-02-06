package com.android.maxclub.bluetoothme.feature_bluetooth.presentation.connection.components

import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.core.graphics.ColorUtils
import com.android.maxclub.bluetoothme.R
import com.android.maxclub.bluetoothme.feature_bluetooth.domain.bluetooth.models.DeviceType
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
    val (containerColor, icon) = when (deviceType) {
        is DeviceType.Classic -> BabyBlue to (R.drawable.ic_bluetooth_classic_24 to R.string.bluetooth_classic_device)
        is DeviceType.Ble -> Violet to (R.drawable.ic_bluetooth_le_24 to R.string.bluetooth_le_device)
        is DeviceType.Dual -> RedOrange to (R.drawable.ic_bluetooth_dual_24 to R.string.bluetooth_dual_device)
        is DeviceType.Unknown -> LightGreen to (R.drawable.ic_bluetooth_unknown_24 to R.string.bluetooth_unknown_device)
    }
    val contentColor =
        Color(ColorUtils.blendARGB(containerColor.toArgb(), Color.Black.toArgb(), 0.5f))
    val painter = painterResource(id = icon.first)
    val contentDescription = stringResource(id = icon.second)

    FilledTonalIconButton(
        onClick = { onClick(contentDescription) },
        colors = IconButtonDefaults.filledTonalIconButtonColors(
            containerColor = containerColor,
            contentColor = contentColor,
        ),
        modifier = modifier,
    ) {
        Icon(
            painter = painter,
            contentDescription = contentDescription,
        )
    }
}