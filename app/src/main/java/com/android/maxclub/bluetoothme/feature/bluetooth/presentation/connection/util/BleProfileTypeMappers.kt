package com.android.maxclub.bluetoothme.feature.bluetooth.presentation.connection.util

import com.android.maxclub.bluetoothme.feature.bluetooth.domain.bluetooth.models.BluetoothLeProfile

fun BluetoothLeProfile.toBleProfileType(): BleProfileType =
    when (this) {
        is BluetoothLeProfile.Default -> BleProfileType.DEFAULT
        is BluetoothLeProfile.Custom -> BleProfileType.CUSTOM
    }