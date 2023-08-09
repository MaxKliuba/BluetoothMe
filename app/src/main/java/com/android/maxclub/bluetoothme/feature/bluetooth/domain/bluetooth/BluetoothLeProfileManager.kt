package com.android.maxclub.bluetoothme.feature.bluetooth.domain.bluetooth

import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattService

abstract class BluetoothLeProfileManager(val gattService: BluetoothGattService) {

    abstract val readCharacteristic: BluetoothGattCharacteristic?

    abstract val writeCharacteristic: BluetoothGattCharacteristic?

    abstract fun connectCharacteristics(): Boolean
}