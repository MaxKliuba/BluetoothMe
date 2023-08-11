package com.android.maxclub.bluetoothme.feature.bluetooth.presentation.connection.util

import androidx.annotation.StringRes
import com.android.maxclub.bluetoothme.R

enum class BleProfileType(@StringRes val titleResId: Int) {
    DEFAULT(R.string.ble_profile_default),
    CUSTOM(R.string.ble_profile_custom),
}