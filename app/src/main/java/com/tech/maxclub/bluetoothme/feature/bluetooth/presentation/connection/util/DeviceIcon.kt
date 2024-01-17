package com.tech.maxclub.bluetoothme.feature.bluetooth.presentation.connection.util

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.ColorUtils
import com.tech.maxclub.bluetoothme.R
import com.tech.maxclub.bluetoothme.feature.bluetooth.domain.bluetooth.models.DeviceType

data class DeviceIcon(
    val backgroundColor: Color,
    @DrawableRes val iconResId: Int,
    val description: String,
) {
    val tintColor: Color
        get() = Color(ColorUtils.blendARGB(backgroundColor.toArgb(), Color.Black.toArgb(), 0.5f))
}

fun DeviceType.toDeviceIcon(context: Context): DeviceIcon =
    when (this) {
        is DeviceType.Classic -> DeviceIcon(
            backgroundColor = com.tech.maxclub.bluetoothme.ui.theme.BabyBlue,
            iconResId = R.drawable.ic_bluetooth_classic_24,
            description = context.getString(R.string.bluetooth_classic_device),
        )

        is DeviceType.Ble -> DeviceIcon(
            backgroundColor = com.tech.maxclub.bluetoothme.ui.theme.Violet,
            iconResId = R.drawable.ic_bluetooth_le_24,
            description = context.getString(R.string.bluetooth_le_device),
        )

        is DeviceType.Dual -> DeviceIcon(
            backgroundColor = com.tech.maxclub.bluetoothme.ui.theme.RedOrange,
            iconResId = R.drawable.ic_bluetooth_dual_24,
            description = context.getString(R.string.bluetooth_dual_device),
        )

        is DeviceType.Unknown -> DeviceIcon(
            backgroundColor = com.tech.maxclub.bluetoothme.ui.theme.LightGreen,
            iconResId = R.drawable.ic_bluetooth_unknown_24,
            description = context.getString(R.string.bluetooth_unknown_device),
        )
    }