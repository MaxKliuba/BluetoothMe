package com.tech.maxclub.bluetoothme.feature.bluetooth.presentation.connection.util

import androidx.annotation.StringRes
import com.tech.maxclub.bluetoothme.R
import com.tech.maxclub.bluetoothme.feature.bluetooth.domain.bluetooth.models.BluetoothLeProfile

enum class BleProfileType(@StringRes val titleResId: Int) {
    DEFAULT(R.string.ble_profile_default_button),
    CUSTOM(R.string.ble_profile_custom_button),
}

fun BluetoothLeProfile.toBleProfileType(): BleProfileType =
    when (this) {
        is BluetoothLeProfile.Default -> BleProfileType.DEFAULT
        is BluetoothLeProfile.Custom -> BleProfileType.CUSTOM
    }