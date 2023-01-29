package com.android.maxclub.bluetoothme.feature_bluetooth.domain.bluetooth.models

import java.util.UUID

sealed class BluetoothLeProfile {
    object Default : BluetoothLeProfile()
    data class Custom(
        val serviceUuid: UUID,
        val readCharacteristicUuid: UUID,
        val writeCharacteristicUuid: UUID,
    ) : BluetoothLeProfile()
}