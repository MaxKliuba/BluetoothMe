package com.android.maxclub.bluetoothme.feature_bluetooth.data.sources.bluetooth

import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattService

abstract class BluetoothLeProfileManager(val gattService: BluetoothGattService) {

    abstract val readCharacteristic: BluetoothGattCharacteristic?

    abstract val writeCharacteristic: BluetoothGattCharacteristic?

    abstract fun connectCharacteristics(gatt: BluetoothGatt): Boolean
}