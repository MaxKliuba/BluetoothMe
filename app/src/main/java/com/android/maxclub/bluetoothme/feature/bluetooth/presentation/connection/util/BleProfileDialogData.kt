package com.android.maxclub.bluetoothme.feature.bluetooth.presentation.connection.util

import com.android.maxclub.bluetoothme.feature.bluetooth.domain.bluetooth.models.BluetoothDevice

data class BleProfileDialogData(
    val device: BluetoothDevice,
    val selectedBleProfileType: BleProfileType,
    val serviceUuid: String,
    val readCharacteristicUuid: String,
    val writeCharacteristicUuid: String,
    val serviceUuidErrorMessage: String? = null,
    val readCharacteristicUuidErrorMessage: String? = null,
    val writeCharacteristicUuidErrorMessage: String? = null,
)